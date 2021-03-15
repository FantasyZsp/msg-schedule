package xyz.mydev.msg.schedule.load.checkpoint;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * 用于即时投递消息的补偿
 *
 * @author zhaosp
 */
public class LocalCheckpointServiceImpl implements CheckpointService {

  public final LocalDateTime DEFAULT_CHECK_POINT = LocalDateTime.of(LocalDate.ofYearDay(2000, 1), LocalTime.MIN);

  /**
   * 默认从最开始的数据扫描
   *
   * @author ZSP
   */
  @Override
  public LocalDateTime defaultStartCheckpoint(String targetTableName) {
    return DEFAULT_CHECK_POINT;
  }

  /**
   * 当前检查点，对于即时消息，可以从15分钟前开始扫描
   *
   * @author ZSP
   */
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
