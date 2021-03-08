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
                           ScheduleTimeEvaluator scheduleTimeEvaluator,
                           PortRouter<T> portRouter) {
    this.messageLoader = messageLoader;
    this.checkpointService = checkpointService;
    this.scheduleTimeEvaluator = scheduleTimeEvaluator;
    this.portRouter = portRouter;
  }

  private final AtomicBoolean appStarted = new AtomicBoolean(false);

  /**
   * 存在同实例服务正在检查点加载
   */
  abstract boolean isScheduling(String targetTableName);

  abstract Lock getScheduleLock(String targetTableName);


  public void scheduleLoadCyclically(String targetTableName) {

    if (!appStarted.get()) {
      log.warn("app is starting, skip this task");
      return;
    }

    Lock scheduleLock = getScheduleLock(targetTableName);
    if (scheduleLock.tryLock()) {
      log.info("lock scheduleLoadCyclically success");
      try {

        List<T> msgListWillSend = loadCyclically(targetTableName);
        log.info("invoke transfer {} msg", msgListWillSend.size());
        transfer(targetTableName, msgListWillSend);
        log.info("invoke schedule business success");

      } catch (Throwable ex) {
        log.error("schedule business ex", ex);
      } finally {
        try {
          scheduleLock.unlock();
        } catch (Throwable ex) {
          log.warn("checkPointLoadLock unlock ex", ex);
        }
      }
    } else {
      log.info("there is a task invoking by other app instance, so skip this one");
    }
  }





  public void loadOnStart(String targetTableName) {
    Lock scheduleLock = getScheduleLock(targetTableName);
    if (scheduleLock.tryLock()) {
      log.info("load delay msg in db when app up");
      try {
        List<T> msgListWillSend = loadFromCheckPoint(targetTableName);
        log.info("invoke transfer {} msg", msgListWillSend.size());
        transfer(targetTableName, msgListWillSend);
        log.info("invoke schedule business success");

      } catch (Throwable ex) {
        log.error("loadFromCheckPoint ex", ex);
      } finally {
        try {
          scheduleLock.unlock();
        } catch (Throwable ex) {
          log.warn("checkPointLoadLock unlock ex", ex);
        }
      }
    } else {
      log.info("there is a task invoking by other app instance, so skip this one");
    }
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

  public synchronized List<T> loadCyclically(String targetTableName) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime formattedStartTime = scheduleTimeEvaluator.formatTimeWithDefaultInterval(now);
    LocalDateTime endTime = formattedStartTime.plusSeconds(scheduleTimeEvaluator.intervalSeconds());
    log.info("scheduleLoader working at [{}] ,formatted at [{}], end at [{}]", now, formattedStartTime, endTime);
    return messageLoader.load(targetTableName, formattedStartTime, endTime);
  }


  /**
   * 锁池初始化
   */
  public void init() {

  }

  public void initLocks() {

  }


}
