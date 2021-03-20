package xyz.mydev.msg.schedule;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointUpdateStrategy;

import java.util.Collection;
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

  private final Collection<CheckpointService> checkpointServicePool;
  private final ScheduledExecutorService scheduledExecutorService;

  public AbstractCheckpointScheduler(Collection<CheckpointService> checkpointServicePool) {
    this.checkpointServicePool = Objects.requireNonNull(checkpointServicePool);


    int size = 0;
    for (CheckpointService checkpointService : checkpointServicePool) {
      size = size + checkpointService.getTableNames().size();
    }
    // TODO 个性化配置线程池
    this.scheduledExecutorService = Executors.newScheduledThreadPool(size, r -> new Thread(r, "cpUpdateThread"));
  }

  public void start() {
    // TODO 调整为外部化配置提供需要调度的列表
    for (CheckpointService checkpointService : checkpointServicePool) {
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
