package xyz.mydev.msg.schedule;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.common.util.PrefixNameThreadFactory;
import xyz.mydev.msg.schedule.load.MessageLoader;
import xyz.mydev.msg.schedule.load.checkpoint.route.CheckpointServiceRouter;
import xyz.mydev.msg.schedule.port.AbstractPorter;
import xyz.mydev.msg.schedule.port.Porter;
import xyz.mydev.msg.schedule.port.route.PorterRouter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 未每个需要调度的表构造对应的调度任务，提交到线程池执行。
 * <p>
 * app启动调度：
 * 即刻提交所有表的一次性调度任务。
 * <p>
 * 周期调度：
 * 初始调度时间：开始时间 对应间隔里的下个开始时间。
 *
 * @author ZSP
 * @see ScheduleTask
 * @see ScheduleTimeEvaluator
 * @see ScheduledTableRegistry
 */
@Slf4j
public class DefaultMainScheduler implements MainScheduler {

  private final PorterRouter porterRouter;
  private final MessageLoader messageLoader;
  private final CheckpointServiceRouter checkpointServiceRouter;

  private ScheduledExecutorService scheduledExecutorService;

  public DefaultMainScheduler(PorterRouter porterRouter,
                              MessageLoader messageLoader,
                              CheckpointServiceRouter checkpointServiceRouter) {

    this.porterRouter = porterRouter;
    this.messageLoader = messageLoader;
    this.checkpointServiceRouter = checkpointServiceRouter;
  }


  public DefaultMainScheduler(PorterRouter porterRouter,
                              MessageLoader messageLoader,
                              CheckpointServiceRouter checkpointServiceRouter,
                              ScheduledExecutorService scheduledExecutorService) {
    this.porterRouter = porterRouter;
    this.messageLoader = messageLoader;
    this.checkpointServiceRouter = checkpointServiceRouter;
    this.scheduledExecutorService = scheduledExecutorService;
  }

  @Override
  public void start() {
    initExecutor();

    for (Porter<?> porter : porterRouter) {
      if (porter instanceof AbstractPorter) {
        ((AbstractPorter<?>) porter).start();
      }
      String tableName = porter.getTargetTableName();
      Runnable startingTask = buildTask(tableName, true);
      scheduledExecutorService.execute(startingTask);

      Runnable scheduleTask = buildTask(tableName, false);
      TableScheduleProperties tableScheduleProperties = Objects.requireNonNull(porter.getTableScheduleProperties());
      long loadIntervalMinutes = tableScheduleProperties.validate().getLoadInterval();
      LocalDateTime snapshotTime = LocalDateTime.now();

      // 延时尽可能小
      long initialDelay = calculateInitialDelay(snapshotTime, tableScheduleProperties);
      scheduledExecutorService.scheduleAtFixedRate(scheduleTask, initialDelay, loadIntervalMinutes * 60_000, TimeUnit.MILLISECONDS);
    }

  }

  @Override
  public void stop() {
    for (Porter<?> porter : porterRouter) {
      porter.shutdown();
    }
  }

  protected void initExecutor() {
    if (scheduledExecutorService == null) {
      this.scheduledExecutorService = Executors.newScheduledThreadPool(porterRouter.size() * 2, new PrefixNameThreadFactory("MnS"));
    }
  }

  /**
   * formattedEndOfSnapshotTime minus snapshotTime
   * TimeUnit.MILLISECONDS
   * 根据当前时间，计算距离下一个调度时间还有多久
   *
   * @return 距离下一个调度时间的毫秒数
   */
  protected long calculateInitialDelay(LocalDateTime snapshotTime, TableScheduleProperties tableScheduleProperties) {
    LocalDateTime intervalStart = ScheduleTimeEvaluator.formatTimeWithInterval(snapshotTime, tableScheduleProperties.getLoadInterval());
    return snapshotTime.until(intervalStart.plusMinutes(tableScheduleProperties.getLoadInterval()), ChronoUnit.MILLIS);
  }

  protected <T> ScheduleTask<T> buildTask(String tableName, boolean isStartingTask) {
    return new ScheduleTask<>(tableName,
      porterRouter.get(tableName),
      messageLoader,
      Objects.requireNonNull(checkpointServiceRouter.get(tableName)),
      isStartingTask ? TaskTimeType.TaskTimeTypeEnum.CheckpointTimeTask : TaskTimeType.TaskTimeTypeEnum.IntervalTimeTask,
      isStartingTask
    );
  }

  public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
    this.scheduledExecutorService = scheduledExecutorService;
  }
}
