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
 * 1. 应用启动时，检查点类型
 * 对于延时消息，从检查点开始扫描到 当前格式化结束时间（间隔的末尾，被格式化后得到的是间隔区间的开始时间，加上间隔后得到结束时间）
 * 对于即时消息，从检查点开始扫描到 当前格式化束时间
 * 2. 定时调度
 * 对于延时消息，从当前格式化开始时间到格式化结束时间
 * 对于即时消息，从当前格式化时间的开始，减去一定的容错时间范围，如一分钟， 到格式化结束时间
 * 并发调度：
 * 当1和2多实例下并发时，启动优于调度。启动间需要争抢锁，调度间需要争抢锁。
 * 调度需要在无应用启动时进入调度逻辑。
 * <p>
 * 3. TODO  即时检查点纠错任务
 * 当检查点检测到距离当前时间大于了一个间隔时，会立即调度，此时开始时间对应检查点给出的时间，结束时间对应当前格式化结束时间。
 * 检查服务可以选择在调用时给出开始和结束时间。
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
  private final TaskTimeTypeEnum taskTimeType;

  /**
   * 标识是否是启动任务
   */
  private final boolean isStartingTask;

  public ScheduleTask(String targetTableName,
                      Porter<T> porter,
                      MessageLoader messageLoader,
                      CheckpointService checkpointService,
                      TaskTimeTypeEnum taskTimeType,
                      boolean isStartingTask) {

    this.targetTableName = targetTableName;
    this.porter = porter;
    this.messageLoader = messageLoader;
    this.checkpointService = checkpointService;
    this.taskTimeType = taskTimeType;
    this.isStartingTask = isStartingTask;
  }

  @Override
  public void run() {

    log.info("schedule for {}, task type: {}, isStartingTask: {}", targetTableName, getTaskTimeType(), isStartingTask());

    LocalDateTime now = LocalDateTime.now();

    LocalDateTime[] localDateTimes = getTime(now);
    LocalDateTime startTime = localDateTimes[0];
    LocalDateTime endTime = localDateTimes[1];

    Lock scheduleLock = messageLoader.getScheduleLock(buildLoadLockName(endTime));

    if (scheduleLock.tryLock()) {
      try {

        List<T> msgListWillSend = load(now, startTime, endTime);

        log.info("invoke transfer {} msg for {}", msgListWillSend.size(), targetTableName);

        transfer(msgListWillSend);

        log.info("invoke transfer success for {}", targetTableName);

      } catch (Throwable ex) {
        log.error("schedule load business ex for {}", targetTableName, ex);
      } finally {
        try {
          scheduleLock.unlock();
        } catch (Throwable ex) {
          log.warn("scheduleLock unlock ex  for {}", targetTableName, ex);
        }
      }
    } else {
      log.info("there is a task for {} invoking by other app instance, so skip this one", targetTableName);
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


    log.info("load for {}, task type [{}], working at [{}] ,formatted at [{}], end at [{}]", targetTableName, getTaskTimeType(), now, startTime, endTime);
    return messageLoader.load(getTargetTableName(), startTime, endTime);
  }


  protected LocalDateTime[] getTime(LocalDateTime now) {
    LocalDateTime startTime;
    LocalDateTime endTime;
    TaskTimeTypeEnum taskTimeType = getTaskTimeType();
    int intervalMinutes = ScheduleTimeEvaluator.getTableLoadIntervalMinutes(targetTableName);

    // checkpoint -> formatted now plus interval
    if (TaskTimeTypeEnum.CheckpointTimeTask.equals(taskTimeType)) {
      startTime = checkpointService.readCheckpoint(getTargetTableName());
      endTime = ScheduleTimeEvaluator.formatTimeWithInterval(now, intervalMinutes).plusMinutes(intervalMinutes);
    } else {
      // formatted now -> plus interval
      startTime = ScheduleTimeEvaluator.formatTimeWithInterval(now, intervalMinutes);
      endTime = startTime.plusSeconds(intervalMinutes);
    }

    if (!ScheduleTimeEvaluator.isDelayTable(targetTableName)) {
      // 即时消息容错处理
      startTime = startTime.minusMinutes(ScheduleTimeEvaluator.getTableLoadIntervalMinutes(targetTableName) / 3);
    }
    return new LocalDateTime[]{startTime, endTime};
  }

  /**
   * 锁key:  ld:tableName:endTimeSequence
   * endTimeSequence格式: 当天间隔顺序号，从0开始。如30分钟，那么结束时间 1:00对应的就是2 = 60/30
   */
  protected String buildLoadLockName(LocalDateTime correctedEndTime) {
    return "ld:" + targetTableName + ":" + ScheduleTimeEvaluator.intervalSequenceNo(correctedEndTime, targetTableName);
  }
}
