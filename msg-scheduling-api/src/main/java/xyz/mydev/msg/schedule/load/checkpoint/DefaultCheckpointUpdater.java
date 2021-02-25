package xyz.mydev.msg.schedule.load.checkpoint;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.load.checkpoint.route.CheckpointServiceRouter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 调度参数配置化
 *
 * @author zhaosp
 */
@Slf4j
public class DefaultCheckpointUpdater {

  private final CheckpointServiceRouter checkpointServiceRouter;

  private final ScheduledExecutorService scheduledExecutorService;

  public DefaultCheckpointUpdater(CheckpointServiceRouter checkpointServiceRouter) {
    this.checkpointServiceRouter = Objects.requireNonNull(checkpointServiceRouter);
    // TODO 自定义线程池：线程池名字、个数，队列大小，拒绝策略
    scheduledExecutorService = Executors.newScheduledThreadPool(checkpointServiceRouter.getMsgTableNames().size(), r -> new Thread(r, "cpUpdateThread"));
    // TODO 线程池初始化策略
  }

  public void startWorking() {
    checkpointServiceRouter.getMap().forEach((tableName, cpService) -> {
      // TODO 自定义线程池
      scheduledExecutorService.scheduleWithFixedDelay(() -> updateCheckpoint(tableName), 2, 30, TimeUnit.MINUTES);
    });
  }

  public void stopWorking() {
    scheduledExecutorService.shutdown();
  }

  private void updateCheckpoint(String targetTableName) {
    CheckpointService checkpointService = checkpointServiceRouter.get(targetTableName);
    Lock scheduleLock = checkpointService.getScheduleLock(targetTableName);

    boolean enableSchedule = scheduleLock.tryLock();

    if (!enableSchedule) {
      log.info("schedule to update checkpoint by other app for {}", targetTableName);
    }

    if (enableSchedule) {
      try {
        Lock writeLock = checkpointService.getReadWriteLock(targetTableName).writeLock();

        // 调度任务拿到的检查点一定是可靠的、最新的，阻塞写入
        writeLock.lock();
        try {
          LocalDateTime next = checkpointService.readNextCheckpoint(targetTableName);
          checkpointService.writeCheckpoint(targetTableName, next);
          log.info("update checkpoint success , {}", next);
        } finally {
          writeLock.unlock();
        }


      } catch (Throwable ex) {
        log.error("update checkpoint failed for {}", targetTableName, ex);
      } finally {
        scheduleLock.unlock();
      }
    }


  }


}