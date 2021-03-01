package xyz.mydev.msg.schedule;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.bean.BaseMessage;
import xyz.mydev.msg.schedule.load.AbstractMessageLoader;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import static xyz.mydev.msg.schedule.load.ScheduleTimeCalculator.formatTime4HalfHour;


/**
 * 调度说明：
 * 先完善加载机制，再完善消费失败记录
 * 1. 应用启动时，从检查点开始扫描到当前格式化时间
 * 2. 定时调度，从当前格式化时间开始到一个间隔后格式化时间
 * 并发调度：
 * 当1和2多实例下并发时，启动优于调度。启动间需要争抢锁，调度间需要争抢锁。
 * 调度需要在无应用启动时进入调度逻辑。
 *
 * @author ZSP
 */
@Slf4j
public abstract class AbstractScheduler<T extends BaseMessage<String>> {
  private final AbstractMessageLoader<T> messageLoader;
  private final CheckpointService checkpointService;

  public AbstractScheduler(AbstractMessageLoader<T> messageLoader,
                           CheckpointService checkpointService) {
    this.messageLoader = messageLoader;
    this.checkpointService = checkpointService;
  }

  private final AtomicBoolean appInitializationCompleted = new AtomicBoolean(false);

  /**
   * 存在同实例服务正在检查点加载
   */
  abstract boolean isLoadingFromCp(String targetTableName);

  abstract String getLockKeyOfLoadingFromCheckpoint(String targetTableName);

  abstract Lock getLockOfLoadingFromCheckpoint(String targetTableName);

  abstract String getLockKeyOfLoadCyclically(String targetTableName);

  abstract Lock getLockOfLoadCyclically(String targetTableName);

  private Lock lockOfLoadingFromCheckpoint;
  private Lock lockOfLoadCyclically;

  /**
   * TODO 1. 线程池定时调度 2. 路由
   */
  public void scheduleLoadCyclically(String targetTableName) {

    if (!appInitializationCompleted.get()) {
      log.warn("app is starting, skip this task");
      return;
    }

    if (isLoadingFromCp(targetTableName)) {
      log.info("there is a service starting, skip this task");
      return;
    }

    if (getLockOfLoadCyclically(targetTableName).tryLock()) {
      log.info("lock LOAD_CYCLICALLY success");
      try {
        // TODO 投递
        List<T> msgListWillSend = loadCyclically(targetTableName);
        log.info("invoke business success");

      } catch (Throwable ex) {
        log.info("business ex");
      } finally {
        getLockOfLoadCyclically(targetTableName).unlock();
      }
    } else {
      log.info("loadLock failed");
    }
  }


  public synchronized List<T> loadCyclically(String targetTableName) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime formattedStartTime = formatTime4HalfHour(now);
    LocalDateTime endTime = formattedStartTime.plusMinutes(30);
    log.info("scheduleLoader working at [{}] ,formatted at [{}], end at [{}]", now, formattedStartTime, endTime);
    return messageLoader.load(targetTableName, formattedStartTime, endTime);
  }


  /**
   * 应用启动时
   */
  public void onStart(String targetTableName) throws Exception {

    if (getLockOfLoadingFromCheckpoint(targetTableName).tryLock()) {
      log.info("load delay msg in db when app up");
      try {
        log.info("invoke loadFromCheckPoint");
        List<T> msgListWillSend = loadFromCheckPoint(targetTableName);
        // TODO 投递
        log.info("invoke loadFromCheckPoint success");

      } catch (Throwable ex) {
        log.error("loadFromCheckPoint ex", ex);
      } finally {
        try {
          getLockOfLoadingFromCheckpoint(targetTableName).unlock();
        } catch (Throwable ex) {
          log.warn("checkPointLoadLock unlock ex", ex);
        }
      }

    } else {
      log.info("delay msg is loading from db by other app instance while I am starting");
    }
    appInitializationCompleted.set(true);
  }

  public List<T> loadFromCheckPoint(String targetTableName) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startTimeFromCheckPoint = checkpointService.readCheckpoint(targetTableName);
    LocalDateTime endTime = formatTime4HalfHour(now).plusMinutes(30);
    log.info("loadFromCheckPoint working at [{}] ,checkpoint  at [{}], end at [{}]", now, startTimeFromCheckPoint, endTime);
    return messageLoader.load(targetTableName, startTimeFromCheckPoint, endTime);

  }


  /**
   * 锁池初始化
   */
  public void init() {

  }

  public void initLocks() {

  }


}
