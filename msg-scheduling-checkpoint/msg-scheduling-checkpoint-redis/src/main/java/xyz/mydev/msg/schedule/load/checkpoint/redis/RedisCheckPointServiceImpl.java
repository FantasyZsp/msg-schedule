package xyz.mydev.msg.schedule.load.checkpoint.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.util.Assert;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointUpdateStrategy;
import xyz.mydev.msg.schedule.load.checkpoint.DefaultCheckpointUpdateStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * 默认 只有延时消息选用此CP
 * <p>
 * 实际上负责了多个表的调度
 *
 * @author ZSP
 */
@Slf4j
public class RedisCheckPointServiceImpl implements CheckpointService {

  public final LocalDateTime DEFAULT_CHECK_POINT = LocalDateTime.of(LocalDate.ofYearDay(2000, 1), LocalTime.MIN);

  /**
   * The ISO date-time formatter that formats or parses a date-time without an offset, such as '2011-12-03T10:15:30'.
   */
  private final Map<String, RBucket<String>> cpHolderPool = new ConcurrentHashMap<>();
  private final Map<String, ReadWriteLock> writeLockPool = new ConcurrentHashMap<>();
  private final Map<String, Lock> scheduleLockPool = new ConcurrentHashMap<>();
  private final RedissonClient redissonClient;


  private final Set<String> tableNames;
  private final MessageRepositoryRouter messageRepositoryRouter;

  public RedisCheckPointServiceImpl(RedissonClient redissonClient,
                                    MessageRepositoryRouter repositoryRouter,
                                    Collection<String> tableNames) {
    Objects.requireNonNull(redissonClient);
    this.messageRepositoryRouter = Objects.requireNonNull(repositoryRouter);
    this.redissonClient = redissonClient;
    this.tableNames = new HashSet<>(tableNames);
  }

  public RedisCheckPointServiceImpl(RedissonClient redissonClient,
                                    MessageRepositoryRouter repositoryRouter) {
    Objects.requireNonNull(redissonClient);
    this.messageRepositoryRouter = Objects.requireNonNull(repositoryRouter);
    this.redissonClient = redissonClient;
    this.tableNames = new HashSet<>();
  }

  @Override
  public void init() {

    Assert.notEmpty(tableNames, "RedisCheckPointServiceImpl tableNames must not be empty");

    Set<String> scheduledTables = messageRepositoryRouter.getScheduledTables();
    Collection<String> subtract = CollectionUtils.subtract(tableNames, scheduledTables);
    if (!subtract.isEmpty()) {
      throw new IllegalStateException("there are some RedisCheckPointServiceImpl tables cannot find repository, as " + subtract);
    }
    initCpHolderPool();
    initWriteLockPool();
    initScheduleLockPool();
  }

  private void initScheduleLockPool() {

    for (String tableName : tableNames) {
      scheduleLockPool.computeIfAbsent(tableName, currentTableName ->
        redissonClient.getLock(getScheduleLockName(currentTableName)));
    }
  }

  private void initWriteLockPool() {
    for (String tableName : this.tableNames) {
      writeLockPool.computeIfAbsent(tableName, currentTableName ->
        redissonClient.getReadWriteLock(getWriteLockName(currentTableName)));
    }
  }

  private void initCpHolderPool() {
    for (String tableName : this.tableNames) {
      cpHolderPool.computeIfAbsent(tableName, currentTableName ->
        redissonClient.getBucket(getCpHolderName(currentTableName)));
    }
  }

  private final CheckpointUpdateStrategy updater = new DefaultCheckpointUpdateStrategy(this);


  /**
   * rcpwl -> redisCheckpointWriteLock
   *
   * @author ZSP
   */
  public static String getWriteLockName(String targetTableName) {
    return "cpwl:" + targetTableName;
  }

  /**
   * rcpsl -> redisCheckpointScheduleLock
   *
   * @author ZSP
   */
  public static String getScheduleLockName(String targetTableName) {
    return "cpsl:" + targetTableName;
  }

  /**
   * rcph -> redisCheckpointHolder
   *
   * @author ZSP
   */
  public static String getCpHolderName(String targetTableName) {
    return "checkpoint:" + targetTableName;
  }


  /**
   * 从缓存读取，如果不存在，返回默认值并修改缓存
   * 不需要关心一致性
   *
   * @author ZSP
   */
  @Override
  public LocalDateTime readCheckpoint(String targetTableName) {
    return getFromHolder(targetTableName)
      .orElseGet(() -> {
        Lock writeLock = writeLockPool.computeIfAbsent(targetTableName, currentTableName ->
          redissonClient.getReadWriteLock(getWriteLockName(currentTableName))).writeLock();
        if (writeLock.tryLock()) {
          try {
            LocalDateTime checkpoint = messageRepositoryRouter.get(targetTableName).findCheckpoint().orElse(defaultStartCheckpoint(targetTableName));
            writeCheckpoint(targetTableName, checkpoint);
            return checkpoint;
          } catch (Throwable ex) {
            log.error("ex when readWriteCheckPoint..", ex);
            return defaultStartCheckpoint(targetTableName);
          } finally {
            writeLock.unlock();
          }
        } else {
          return defaultStartCheckpoint(targetTableName);
        }
      });
  }

  /**
   * 从db查询并返回下一个检查点，如果不存在，就返回当前时间点。
   *
   * @author ZSP
   */
  @Override
  public LocalDateTime loadNextCheckpoint(String targetTableName, LocalDateTime currentCheckPoint) {
    Objects.requireNonNull(currentCheckPoint);
    Optional<LocalDateTime> nextCheckpoint = messageRepositoryRouter.get(targetTableName).findNextCheckpointAfter(currentCheckPoint);
    log.info("{} currentCheckPoint: {}, nextCheckpoint: {}", targetTableName, currentCheckPoint, nextCheckpoint);
    return nextCheckpoint.orElse(currentCheckPoint);
  }

  @Override
  public LocalDateTime loadNextCheckpoint(String targetTableName) {
    return getFromHolder(targetTableName)
      .or(() -> Optional.of(defaultStartCheckpoint(targetTableName)))
      .map(currentCp -> loadNextCheckpoint(targetTableName, currentCp))
      // never null
      .get();
  }

  /**
   * 从holder中取值，可能不存在
   */
  public Optional<LocalDateTime> getFromHolder(String targetTableName) {
    RBucket<String> cpHolder = cpHolderPool.get(targetTableName);
    return Optional.ofNullable(cpHolder.get()).map(point -> LocalDateTime.parse(point, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
  }


  @Override
  public ReadWriteLock getReadWriteLock(String targetTableName) {
    return writeLockPool.computeIfAbsent(targetTableName, currentTableName ->
      redissonClient.getReadWriteLock(getWriteLockName(currentTableName)));
  }

  @Override
  public LocalDateTime defaultStartCheckpoint(String targetTableName) {
    return DEFAULT_CHECK_POINT;
  }


  @Override
  public void writeCheckpoint(String targetTableName, LocalDateTime checkpoint) {
    Objects.requireNonNull(checkpoint);
    cpHolderPool.get(targetTableName).set(checkpoint.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
  }

  @Override
  public Lock getScheduleLock(String targetTableName) {
    return scheduleLockPool.computeIfAbsent(targetTableName, currentTableName ->
      redissonClient.getLock(getScheduleLockName(currentTableName)));
  }

  @Override
  public Set<String> getTableNames() {
    return tableNames;
  }

  @Override
  public CheckpointUpdateStrategy getUpdateStrategy(String targetTableName) {
    return updater;
  }
}
