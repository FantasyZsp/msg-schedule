package xyz.mydev.msg.schedule;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.load.AbstractMessageLoader;
import xyz.mydev.msg.schedule.load.ScheduleTimeEvaluator;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;
import xyz.mydev.msg.schedule.port.AbstractPorter;
import xyz.mydev.msg.schedule.port.route.PortRouter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;


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
public abstract class AbstractScheduler<T extends StringMessage> {
  private final AbstractMessageLoader<T> messageLoader;
  private final CheckpointService checkpointService;
  private final ScheduleTimeEvaluator scheduleTimeEvaluator;
  private final PortRouter<T> portRouter;

  public AbstractScheduler(AbstractMessageLoader<T> messageLoader,
                           CheckpointService checkpointService,
                           ScheduleTimeEvaluator scheduleTimeEvaluator, PortRouter<T> portRouter) {
    this.messageLoader = messageLoader;
    this.checkpointService = checkpointService;
    this.scheduleTimeEvaluator = scheduleTimeEvaluator;
    this.portRouter = portRouter;
  }

  private final AtomicBoolean appStarted = new AtomicBoolean(false);

  /**
   * 存在同实例服务正在检查点加载
   */
  abstract boolean isAppUpLoading(String targetTableName);

  abstract String getAppUpLoadingLockKey(String targetTableName);

  abstract Lock getAppUpLoadingLock(String targetTableName);

  abstract String getLoadCyclicallyLockKey(String targetTableName);

  abstract Lock getLoadCyclicallyLock(String targetTableName);

  /**
   * TODO 1. 线程池定时调度 2. 路由
   */
  public void scheduleLoadCyclically(String targetTableName) {

    if (!appStarted.get()) {
      log.warn("app is starting, skip this task");
      return;
    }

    if (isAppUpLoading(targetTableName)) {
      log.info("there is a service starting, skip this task");
      return;
    }

    Lock loadCyclicallyLock = getLoadCyclicallyLock(targetTableName);

    if (loadCyclicallyLock.tryLock()) {
      log.info("lock LOAD_CYCLICALLY success");
      try {

        List<T> msgListWillSend = loadCyclically(targetTableName);

        transfer(targetTableName, msgListWillSend);

        log.info("invoke schedule business success");

      } catch (Throwable ex) {
        log.info("schedule business ex");
      } finally {
        loadCyclicallyLock.unlock();
      }
    } else {
      log.info("loadLock failed");
    }
  }


  public synchronized List<T> loadCyclically(String targetTableName) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime formattedStartTime = scheduleTimeEvaluator.formatTimeWithDefaultInterval(now);
    LocalDateTime endTime = formattedStartTime.plusSeconds(scheduleTimeEvaluator.intervalSeconds());
    log.info("scheduleLoader working at [{}] ,formatted at [{}], end at [{}]", now, formattedStartTime, endTime);
    return messageLoader.load(targetTableName, formattedStartTime, endTime);
  }


  /**
   * 应用启动时
   */
  public void onStart(String targetTableName) throws Exception {

    if (getAppUpLoadingLock(targetTableName).tryLock()) {
      log.info("load delay msg in db when app up");
      try {
        log.info("invoke loadFromCheckPoint");
        List<T> msgListWillSend = loadFromCheckPoint(targetTableName);

        transfer(targetTableName, msgListWillSend);

        log.info("invoke loadFromCheckPoint success");

      } catch (Throwable ex) {
        log.error("loadFromCheckPoint ex", ex);
      } finally {
        try {
          getAppUpLoadingLock(targetTableName).unlock();
        } catch (Throwable ex) {
          log.warn("checkPointLoadLock unlock ex", ex);
        }
      }

    } else {
      log.info("delay msg is loading from db by other app instance while I am starting");
    }
    appStarted.set(true);
  }

  private void transfer(String targetTableName, List<T> msgListWillSend) {
    AbstractPorter<T> porter = portRouter.get(targetTableName);
    for (T msg : msgListWillSend) {
      porter.transfer(msg);
    }
  }

  public List<T> loadFromCheckPoint(String targetTableName) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startTimeFromCheckPoint = checkpointService.readCheckpoint(targetTableName);
    LocalDateTime endTime = scheduleTimeEvaluator.formatTimeWithDefaultInterval(now).plusSeconds(scheduleTimeEvaluator.intervalSeconds());
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
