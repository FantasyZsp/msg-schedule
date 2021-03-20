package xyz.mydev.msg.schedule;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.load.DefaultMessageLoader;
import xyz.mydev.msg.schedule.load.ScheduleTimeEvaluator;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;
import xyz.mydev.msg.schedule.port.Porter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

/**
 * TODO ING
 *
 * @author ZSP
 */
@Slf4j
public class ScheduleTask implements Runnable, TaskTimeType {

  private final String targetTableName;
  private final Porter<? super StringMessage> porter;
  private final DefaultMessageLoader<? extends StringMessage> messageLoader;
  /**
   * TODO 可能和targetTableName不匹配
   * 二选一
   * 1. 外层调用保证
   * 2. 设计上避免
   */
  private final CheckpointService checkpointService;


  private final ScheduleTimeEvaluator scheduleTimeEvaluator;

  private final AtomicBoolean appStarted;

  /**
   * 锁本质上是在控制加载，考虑放到 MessageLoader
   */
  private final Lock scheduleLock;

  private final TaskTimeTypeEnum taskTimeType;


  @Setter
  private boolean invokeWhenAppStart;

  public ScheduleTask(String targetTableName,
                      Porter<? super StringMessage> porter,
                      DefaultMessageLoader<? extends StringMessage> messageLoader,
                      CheckpointService checkpointService,
                      ScheduleTimeEvaluator scheduleTimeEvaluator,

                      AtomicBoolean appStarted,
                      Lock scheduleLock,
                      TaskTimeTypeEnum taskTimeType,
                      boolean invokeWhenAppStart) {

    this.targetTableName = targetTableName;
    this.porter = porter;
    this.messageLoader = messageLoader;
    this.checkpointService = checkpointService;
    this.scheduleTimeEvaluator = scheduleTimeEvaluator;
    this.appStarted = appStarted;
    this.scheduleLock = scheduleLock;
    this.taskTimeType = taskTimeType;
    this.invokeWhenAppStart = invokeWhenAppStart;
  }

  @Override
  public void run() {

    // 非启动任务需要关注启动标志，仅当启动完毕后才可以执行
    if (!invokeWhenAppStart) {
      if (!appStarted.get()) {
        log.warn("app is starting, skip this task");
        return;
      }
    }

    log.info("task type: {}", getTaskTimeType());

    if (scheduleLock.tryLock()) {
      try {

        List<? extends StringMessage> msgListWillSend = load(targetTableName);

        log.info("invoke transfer {} msg", msgListWillSend.size());

        transfer(msgListWillSend);

        log.info("invoke schedule business success");

      } catch (Throwable ex) {
        log.error("schedule business ex", ex);
      } finally {
        try {
          scheduleLock.unlock();
        } catch (Throwable ex) {
          log.warn("scheduleLock unlock ex", ex);
        }
      }
    } else {
      log.info("there is a task invoking by other app instance, so skip this one");
    }

    invokeWhenAppStart = false;
  }


  private void transfer(List<? extends StringMessage> msgListWillSend) {
    for (StringMessage stringMessage : msgListWillSend) {
      porter.transfer(stringMessage);
    }
  }


  /**
   * 加载目标表消息
   * 时段的产生，依赖于各个表的配置。一般有下面的类型：
   * <p>
   * 延时类
   * 1. checkpoint -> formatted now plus interval
   * 2. formatted now -> plus interval
   * 即时类
   * 1. checkpoint -> formatted now plus interval
   * 2. formatted now -> plus interval
   */
  private List<? extends StringMessage> load(String targetTableName) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startTime;
    LocalDateTime endTime;
    long intervalSeconds = scheduleTimeEvaluator.intervalSeconds();

    TaskTimeTypeEnum taskTimeType = getTaskTimeType();

    // TODO 关于 表加载时间范围的外部化配置与策略类实现
    // checkpoint -> formatted now plus interval
    if (TaskTimeTypeEnum.CheckpointTimeTask.equals(taskTimeType)) {

      startTime = checkpointService.readCheckpoint(targetTableName);
      endTime = scheduleTimeEvaluator.formatTimeWithDefaultInterval(now).plusSeconds(intervalSeconds);

    } else {
      // formatted now -> plus interval
      startTime = scheduleTimeEvaluator.formatTimeWithDefaultInterval(now);
      endTime = startTime.plusSeconds(intervalSeconds);
    }

    log.info("task type [{}], working at [{}] ,formatted at [{}], end at [{}]", taskTimeType, now, startTime, endTime);
    return messageLoader.load(targetTableName, startTime, endTime);
  }


  @Override
  public TaskTimeTypeEnum getTaskTimeType() {
    return taskTimeType;
  }
}
