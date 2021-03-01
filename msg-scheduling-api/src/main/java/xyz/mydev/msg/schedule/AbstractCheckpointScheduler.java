package xyz.mydev.msg.schedule;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointUpdateStrategy;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


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
public abstract class AbstractCheckpointScheduler {

  private final CheckpointService checkpointService;

  private final ScheduledExecutorService scheduledExecutorService;

  public AbstractCheckpointScheduler(CheckpointService checkpointService) {
    this.checkpointService = Objects.requireNonNull(checkpointService);
    this.scheduledExecutorService = Executors.newScheduledThreadPool(checkpointService.getTableNames().size(), r -> new Thread(r, "cpUpdateThread"));
  }

  public void startScheduleCheckpoint() {
    checkpointService.getTableNames().forEach(tableName -> {
      CheckpointUpdateStrategy updateStrategy = checkpointService.getUpdateStrategy(tableName);
      if (updateStrategy != null) {
        scheduledExecutorService.scheduleWithFixedDelay(() -> updateStrategy.updateCheckpoint(tableName), 2, 30, TimeUnit.MINUTES);
      }
    });
  }

  public void stop() {
    scheduledExecutorService.shutdown();
  }

}
