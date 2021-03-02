package xyz.mydev.msg.schedule.load.checkpoint;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

/**
 * 调度参数配置化
 *
 * @author zhaosp
 */
@Slf4j
public class DefaultCheckpointUpdateStrategy implements CheckpointUpdateStrategy {

  private final CheckpointService checkpointService;

  public DefaultCheckpointUpdateStrategy(CheckpointService checkpointService) {
    this.checkpointService = Objects.requireNonNull(checkpointService);
  }


  @Override
  public void updateCheckpoint(String targetTableName) {
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