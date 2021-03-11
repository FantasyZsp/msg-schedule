package xyz.mydev.msg.schedule.load.checkpoint;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
  private final MessageRepositoryRouter<? extends StringMessage> messageRepositoryRouter;

  public RedisCheckPointServiceImpl(RedissonClient redissonClient,
                                    MessageRepositoryRouter<? extends StringMessage> repositoryRouter,
                                    Set<String> tableNames) {
    Objects.requireNonNull(redissonClient);
    this.messageRepositoryRouter = Objects.requireNonNull(repositoryRouter);
    this.redissonClient = redissonClient;

    // 初始化
    initCpHolderPool(tableNames);
    initWriteLockPool(tableNames);
    initScheduleLockPool(tableNames);

  }

  private void initScheduleLockPool(Set<String> tableNames) {

    for (String tableName : tableNames) {
      scheduleLockPool.computeIfAbsent(tableName, currentTableName ->
        redissonClient.getLock(getScheduleLockName(tableName)));
    }
  }

  private void initWriteLockPool(Set<String> tableNames) {
    for (String tableName : tableNames) {
      writeLockPool.computeIfAbsent(tableName, currentTableName ->
        redissonClient.getReadWriteLock(getWriteLockName(tableName)));
    }
  }

  private void initCpHolderPool(Set<String> tableNames) {
    for (String tableName : tableNames) {
      cpHolderPool.computeIfAbsent(tableName, currentTableName ->
        redissonClient.getBucket(getCpHolderName(tableName)));
    }
  }

  // TODO 更新策略实现
  private final CheckPointUpdater updater = new CheckPointUpdater(this);


  /**
   * rcpwl -> redisCheckpointWriteLock
   *
   * @author ZSP
   */
  public static String getWriteLockName(String targetTableName) {
    return "rcpwl:" + targetTableName;
  }

  /**
   * rcpsl -> redisCheckpointScheduleLock
   *
   * @author ZSP
   */
  public static String getScheduleLockName(String targetTableName) {
    return "rcpsl:" + targetTableName;
  }

  /**
   * rcph -> redisCheckpointHolder
   *
   * @author ZSP
   */
  public static String getCpHolderName(String targetTableName) {
    return "rcph:" + targetTableName;
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
        Lock writeLock = writeLockPool.get(targetTableName).writeLock();
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
    log.info("currentCheckPoint: {}, nextCheckpoint: {}", currentCheckPoint, nextCheckpoint);
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
    return writeLockPool.get(getWriteLockName(targetTableName));
  }

  public void schedule() {
    updater.startWorking();
  }

  public void stop() {
    updater.stopWorking();
  }

  @Override
  public LocalDateTime defaultStartCheckpoint(String targetTableName) {
    return DEFAULT_CHECK_POINT;
  }


  @Override
  public void writeCheckpoint(String targetTableName, LocalDateTime checkpoint) {
    Objects.requireNonNull(checkpoint);
    cpHolderPool.get(targetTableName).set(checkpoint.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    log.info("writeCheckpoint for {}: {}", targetTableName, checkpoint);
  }

  @Override
  public Lock getScheduleLock(String targetTableName) {
    return scheduleLockPool.get(targetTableName);
  }

  @Override
  public List<String> getTableNames() {
    return null;
  }

  @Override
  public CheckpointUpdateStrategy getUpdateStrategy(String targetTableName) {
    return null;
  }

  static class CheckPointUpdater {

    private final CheckpointService checkPointService;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "checkPointScheduleThread"));

    CheckPointUpdater(CheckpointService checkPointService) {
      this.checkPointService = checkPointService;
    }

    public void startWorking() {
      scheduledExecutorService.scheduleWithFixedDelay(this::updateCheckpoint, 2, 30, TimeUnit.MINUTES);
    }

    public void stopWorking() {
      scheduledExecutorService.shutdown();
    }

    private void updateCheckpoint() {

      Lock scheduleLock = checkPointService.getScheduleLock();

      boolean enableSchedule = scheduleLock.tryLock();

      if (!enableSchedule) {
        log.info("schedule to update checkpoint by other app");
      }

      if (enableSchedule) {
        try {
          Lock writeLock = checkPointService.getScheduleLock();

          // 调度任务拿到的检查点一定是可靠的、最新的，阻塞写入
          writeLock.lock();
          try {
            LocalDateTime next = checkPointService.loadNextCheckpoint();
            checkPointService.writeCheckPoint(next);
            log.info("update checkpoint success , {}", next);
          } finally {
            writeLock.unlock();
          }


        } catch (Throwable ex) {
          log.error("update checkpoint failed", ex);
        } finally {
          scheduleLock.unlock();
        }
      }


    }


  }
}
