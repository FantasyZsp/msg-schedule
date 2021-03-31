package xyz.mydev.msg.schedule;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.load.MessageLoader;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;
import xyz.mydev.msg.schedule.port.Porter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * 调度任务说明：
 * 任务类型 TaskTimeType：
 * 1. 应用启动时，从检查点开始扫描到当前格式化时间
 * 2. 定时调度，从当前格式化时间开始到一个间隔后格式化时间
 * 并发调度：
 * 当1和2多实例下并发时，启动优于调度。启动间需要争抢锁，调度间需要争抢锁。
 * 调度需要在无应用启动时进入调度逻辑。
 * <p>
 *
 * @author ZSP
 */
@Slf4j
@Getter
public class ScheduleTask<T> implements Runnable, TaskTimeType {

  /**
   * 一个任务专为一个表而服务
   */
  private final String targetTableName;
  private final Porter<T> porter;
  private final MessageLoader messageLoader;

  private final CheckpointService checkpointService;

  private final ScheduleTimeEvaluator scheduleTimeEvaluator;

  private final TaskTimeTypeEnum taskTimeType;

  /**
   * 标识是否是启动任务
   */
  private final boolean isStartingTask;

  public ScheduleTask(String targetTableName,
                      Porter<T> porter,
                      MessageLoader messageLoader,
                      CheckpointService checkpointService,
                      ScheduleTimeEvaluator scheduleTimeEvaluator,
                      TaskTimeTypeEnum taskTimeType,
                      boolean isStartingTask) {

    this.targetTableName = targetTableName;
    this.porter = porter;
    this.messageLoader = messageLoader;
    this.checkpointService = checkpointService;
    this.scheduleTimeEvaluator = scheduleTimeEvaluator;
    this.taskTimeType = taskTimeType;
    this.isStartingTask = isStartingTask;
  }

  @Override
  public void run() {

    log.info("task type: {}, isStartingTask: {}", getTaskTimeType(), isStartingTask());

    LocalDateTime now = LocalDateTime.now();

    LocalDateTime[] localDateTimes = getTime(now);
    LocalDateTime startTime = localDateTimes[0];
    LocalDateTime endTime = localDateTimes[1];

    Lock scheduleLock = messageLoader.getScheduleLock(buildLoadLockName(endTime));

    if (scheduleLock.tryLock()) {
      try {

        List<T> msgListWillSend = load(now, startTime, endTime);

        log.info("invoke transfer {} msg", msgListWillSend.size());

        transfer(msgListWillSend);

        log.info("invoke transfer success");

      } catch (Throwable ex) {
        log.error("schedule load business ex", ex);
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
  }


  protected void transfer(List<T> msgListWillSend) {
    for (T stringMessage : msgListWillSend) {
      porter.transfer(stringMessage);
    }
  }


  protected List<T> load(LocalDateTime now,
                         LocalDateTime startTime,
                         LocalDateTime endTime) {


    log.info("task type [{}], working at [{}] ,formatted at [{}], end at [{}]", getTaskTimeType(), now, startTime, endTime);
    return messageLoader.load(getTargetTableName(), startTime, endTime);
  }


  protected LocalDateTime[] getTime(LocalDateTime now) {
    LocalDateTime startTime;
    LocalDateTime endTime;
    TaskTimeTypeEnum taskTimeType = getTaskTimeType();
    int intervalMinutes = scheduleTimeEvaluator.getTableIntervalMinutes(targetTableName);

    // checkpoint -> formatted now plus interval
    if (TaskTimeTypeEnum.CheckpointTimeTask.equals(taskTimeType)) {
      startTime = checkpointService.readCheckpoint(getTargetTableName());
      endTime = scheduleTimeEvaluator.formatTimeWithInterval(now, intervalMinutes).plusMinutes(intervalMinutes);
    } else {
      // formatted now -> plus interval
      startTime = scheduleTimeEvaluator.formatTimeWithInterval(now, intervalMinutes);
      endTime = startTime.plusSeconds(intervalMinutes);
    }
    return new LocalDateTime[]{startTime, endTime};
  }

  /**
   * 锁key:  ld:tableName:endTimeSequence
   * endTimeSequence格式: 当天间隔顺序号，从0开始。如30分钟，那么结束时间 1:00对应的就是2 = 60/30
   */
  protected String buildLoadLockName(LocalDateTime correctedEndTime) {
    return "ld:" + targetTableName + ":" + scheduleTimeEvaluator.intervalSequenceNo(targetTableName, correctedEndTime);
  }
}
