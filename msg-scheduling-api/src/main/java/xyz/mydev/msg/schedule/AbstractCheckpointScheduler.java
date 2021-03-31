package xyz.mydev.msg.schedule;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointUpdateStrategy;
import xyz.mydev.msg.schedule.load.checkpoint.route.CheckpointServiceRouter;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 定时维护检查点
 *
 * @author ZSP
 */
@Slf4j
public abstract class AbstractCheckpointScheduler {

  private final CheckpointServiceRouter checkpointServiceRouter;
  private final ScheduledExecutorService scheduledExecutorService;

  public AbstractCheckpointScheduler(CheckpointServiceRouter checkpointServiceRouter) {
    this.checkpointServiceRouter = Objects.requireNonNull(checkpointServiceRouter);


    int size = checkpointServiceRouter.size();
    // TODO 个性化配置线程池
    this.scheduledExecutorService = Executors.newScheduledThreadPool(size, r -> new Thread(r, "cpUpdateThread"));
  }

  public AbstractCheckpointScheduler(CheckpointServiceRouter checkpointServiceRouter, ScheduledExecutorService scheduledExecutorService) {
    this.checkpointServiceRouter = Objects.requireNonNull(checkpointServiceRouter);
    this.scheduledExecutorService = Objects.requireNonNull(scheduledExecutorService);
  }

  public void start() {
    // TODO 外部化配置调度参数
    for (CheckpointService checkpointService : checkpointServiceRouter) {
      checkpointService.getTableNames().forEach(tableName -> {
        CheckpointUpdateStrategy updateStrategy = checkpointService.getUpdateStrategy(tableName);
        if (updateStrategy != null) {
          scheduledExecutorService.scheduleWithFixedDelay(() -> updateStrategy.updateCheckpoint(tableName), 2, 30, TimeUnit.MINUTES);
        }
      });
    }
  }

  public void stop() {
    scheduledExecutorService.shutdown();
  }
}
