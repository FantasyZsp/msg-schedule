package xyz.mydev.msg.schedule;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.common.util.PrefixNameThreadFactory;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointUpdateStrategy;
import xyz.mydev.msg.schedule.load.checkpoint.route.CheckpointServiceRouter;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


/**
 * 定时维护检查点
 * TODO 与调度联动
 *
 * @author ZSP
 */
@Slf4j
public class DefaultCheckpointScheduler implements CheckpointScheduler {

  private final CheckpointServiceRouter checkpointServiceRouter;
  private ScheduledExecutorService scheduledExecutorService;

  public DefaultCheckpointScheduler(CheckpointServiceRouter checkpointServiceRouter) {
    this.checkpointServiceRouter = checkpointServiceRouter;
    int size = checkpointServiceRouter.size();
    this.scheduledExecutorService = Executors.newScheduledThreadPool(size, new PrefixNameThreadFactory("CpScheduler"));
  }

  public DefaultCheckpointScheduler(CheckpointServiceRouter checkpointServiceRouter, ScheduledExecutorService scheduledExecutorService) {
    this.checkpointServiceRouter = Objects.requireNonNull(checkpointServiceRouter);
    this.scheduledExecutorService = Objects.requireNonNull(scheduledExecutorService);
  }

  @Override
  public void start() {
    for (CheckpointService checkpointService : checkpointServiceRouter) {
      checkpointService.getTableNames().forEach(tableName -> {
        CheckpointUpdateStrategy updateStrategy = checkpointService.getUpdateStrategy(tableName);
        if (updateStrategy != null) {
          scheduledExecutorService.scheduleWithFixedDelay(() -> updateStrategy.updateCheckpoint(tableName), ThreadLocalRandom.current().nextInt(1000, 10000), ScheduledTableRegistry.getCheckpointIntervalMinutes(tableName) * 60_000, TimeUnit.MILLISECONDS);
        }
      });
    }
  }

  @Override
  public void stop() {
    scheduledExecutorService.shutdown();
  }

  public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
    this.scheduledExecutorService = scheduledExecutorService;
  }
}
