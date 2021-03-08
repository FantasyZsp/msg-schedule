package xyz.mydev.msg.schedule;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.load.AbstractMessageLoader;
import xyz.mydev.msg.schedule.load.ScheduleTimeEvaluator;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;
import xyz.mydev.msg.schedule.port.Porter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

/**
 * @author ZSP
 */
@Slf4j
public class ScheduleTask implements Runnable, TaskTimeType {

  private final String targetTableName;
  private final Porter<? super StringMessage> porter;
  private final AbstractMessageLoader<? extends StringMessage> messageLoader;
  private final CheckpointService checkpointService;


  private final ScheduleTimeEvaluator scheduleTimeEvaluator;

  private final AtomicBoolean appStarted;

  private final Lock scheduleLock;

  private final TaskTimeTypeEnum taskTimeType;


  @Setter
  private boolean invokeWhenAppStart;

  public ScheduleTask(String targetTableName,
                      Porter<? super StringMessage> porter,
                      AbstractMessageLoader<? extends StringMessage> messageLoader,
                      CheckpointService checkpointService, ScheduleTimeEvaluator scheduleTimeEvaluator,

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

    invokeWhenAppStart = Boolean.FALSE;

  }


  private void transfer(List<? extends StringMessage> msgListWillSend) {
    for (StringMessage stringMessage : msgListWillSend) {
      porter.transfer(stringMessage);
    }
  }

  private List<? extends StringMessage> load(String targetTableName) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startTime;
    LocalDateTime endTime;
    long intervalSeconds = scheduleTimeEvaluator.intervalSeconds();

    // checkpoint -> formatted now
    if (TaskTimeTypeEnum.CheckpointTimeTask.equals(getTaskTimeType())) {

      startTime = checkpointService.readCheckpoint(targetTableName);

      endTime = scheduleTimeEvaluator.formatTimeWithDefaultInterval(now).plusSeconds(intervalSeconds);
      log.info("loadFromCheckPoint working at [{}] ,checkpoint  at [{}], end at [{}]", now, startTime, endTime);

    } else {
      // formatted now -> plus interval
      startTime = scheduleTimeEvaluator.formatTimeWithDefaultInterval(now);
      endTime = startTime.plusSeconds(intervalSeconds);
      log.info("scheduleLoader working at [{}] ,formatted at [{}], end at [{}]", now, startTime, endTime);
    }

    return messageLoader.load(targetTableName, startTime, endTime);
  }

  /**
   * 根据外部化调度配置，实现指定间隔和频次的加载
   * 这里需要给出最终得到的 时间加载区间
   */
  protected LocalDateTime getLoadStartTime(String targetTableName, LocalDateTime processTime) {

    LocalDateTime startTime;

    if (TaskTimeTypeEnum.CheckpointTimeTask.equals(getTaskTimeType())) {
      startTime = checkpointService.readCheckpoint(targetTableName);
    } else {
      startTime = scheduleTimeEvaluator.formatTimeWithDefaultInterval(processTime);
    }

    return startTime;
  }

  protected LocalDateTime[] getLoadEndTime(String targetTableName, LocalDateTime processTime, LocalDateTime startTime) {

    LocalDateTime endTime = null;

    if (TaskTimeTypeEnum.CheckpointTimeTask.equals(getTaskTimeType())) {
      startTime = checkpointService.readCheckpoint(targetTableName);
    }


    return new LocalDateTime[]{startTime, startTime};
  }


  @Override
  public TaskTimeTypeEnum getTaskTimeType() {
    return taskTimeType;
  }
}
