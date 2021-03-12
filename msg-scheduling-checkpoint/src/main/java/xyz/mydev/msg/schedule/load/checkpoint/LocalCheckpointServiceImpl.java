package xyz.mydev.msg.schedule.load.checkpoint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author zhaosp
 */
public class LocalCheckpointServiceImpl implements CheckpointService {
  @Override
  public LocalDateTime defaultStartCheckpoint(String targetTableName) {
    return null;
  }

  @Override
  public LocalDateTime readCheckpoint(String targetTableName) {
    return null;
  }

  @Override
  public LocalDateTime loadNextCheckpoint(String targetTableName, LocalDateTime currentCheckpoint) {
    return null;
  }

  @Override
  public LocalDateTime loadNextCheckpoint(String targetTableName) {
    return null;
  }

  @Override
  public void writeCheckpoint(String targetTableName, LocalDateTime checkpoint) {

  }

  @Override
  public ReadWriteLock getReadWriteLock(String targetTableName) {
    return null;
  }

  @Override
  public Lock getScheduleLock(String targetTableName) {
    return null;
  }

  @Override
  public List<String> getTableNames() {
    return null;
  }

  @Override
  public CheckpointUpdateStrategy getUpdateStrategy(String targetTableName) {
    return null;
  }
}
