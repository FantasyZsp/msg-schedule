package xyz.mydev.msg.schedule;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.load.MessageLoader;
import xyz.mydev.msg.schedule.load.checkpoint.route.CheckpointServiceRouter;
import xyz.mydev.msg.schedule.port.route.PorterRouter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 未每个需要调度的表构造对应的调度任务，提交到线程池执行
 *
 * @author ZSP
 */
@Slf4j
public abstract class AbstractScheduler {
  private final Map<String, Class<?>> scheduledTablesClassMap;

  private final ScheduleTimeEvaluator scheduleTimeEvaluator;
  private final PorterRouter porterRouter;
  private final MessageLoader messageLoader;
  private final CheckpointServiceRouter checkpointServiceRouter;


  private final ScheduledExecutorService scheduledExecutorService;

  public AbstractScheduler(Map<String, Class<?>> scheduledTablesClassMap,
                           ScheduleTimeEvaluator scheduleTimeEvaluator,
                           PorterRouter porterRouter,
                           MessageLoader messageLoader,
                           CheckpointServiceRouter checkpointServiceRouter) {
    this.scheduledTablesClassMap = scheduledTablesClassMap;

    this.scheduleTimeEvaluator = scheduleTimeEvaluator;
    this.porterRouter = porterRouter;
    this.messageLoader = messageLoader;

    // TODO 个性化配置线程池
    this.scheduledExecutorService = Executors.newScheduledThreadPool(scheduledTablesClassMap.size(), r -> new Thread(r, "Scheduler"));
    this.checkpointServiceRouter = checkpointServiceRouter;
  }

  public void start() {

    LocalDateTime snapshotTime = LocalDateTime.now();

    for (Map.Entry<String, Class<?>> entry : scheduledTablesClassMap.entrySet()) {
      String tableName = entry.getKey();
      Runnable startingTask = buildTask(tableName, true);
      scheduledExecutorService.execute(startingTask);

      Runnable scheduleTask = buildTask(tableName, false);
      // TODO 根据外部化配置，拿到当前表的调度间隔
      long period = 30;
      long initialDelay = calculateInitialDelay(snapshotTime, tableName);
      scheduledExecutorService.scheduleAtFixedRate(scheduleTask, initialDelay, period, TimeUnit.MILLISECONDS);
    }

  }

  /**
   * formattedEndOfSnapshotTime minus snapshotTime
   * TimeUnit.MILLISECONDS
   * TODO 格式化时间策略
   * 根据当前时间，计算距离下一个调度时间还有多久
   *
   * @return 距离下一个调度时间的毫秒数
   */
  protected long calculateInitialDelay(LocalDateTime snapshotTime, String tableName) {
    return 0;
  }

  protected <T> ScheduleTask<T> buildTask(String tableName, boolean isStartingTask) {
    return new ScheduleTask<>(tableName,
      porterRouter.get(tableName),
      messageLoader,
      Objects.requireNonNull(checkpointServiceRouter.get(tableName)),
      scheduleTimeEvaluator,
      isStartingTask ? TaskTimeType.TaskTimeTypeEnum.CheckpointTimeTask : TaskTimeType.TaskTimeTypeEnum.IntervalTimeTask,
      isStartingTask
    );
  }


}
