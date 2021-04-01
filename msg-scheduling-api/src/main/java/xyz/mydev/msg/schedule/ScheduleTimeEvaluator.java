package xyz.mydev.msg.schedule;

import xyz.mydev.msg.common.util.TimeIntervalCorrector;

import java.time.LocalDateTime;

/**
 * 调度时间评估器
 * 根据当前时间和任务的执行时间需求，计算对应的调度策略。
 *
 * @author zhaosp
 */
public class ScheduleTimeEvaluator {

  /**
   * 根据当前时间和间隔，计算当前时间对应的间隔区间的开始时间
   *
   * @param now             即刻时间
   * @param intervalMinutes 间隔时间
   * @return 返回格式化的间隔开始时间
   */
  public LocalDateTime formatTimeWithInterval(LocalDateTime now, Integer intervalMinutes) {
    return TimeIntervalCorrector.formatTimeInterval(now, intervalMinutes);
  }

  public LocalDateTime formatTimeWithTable(String tableName, LocalDateTime now) {
    return TimeIntervalCorrector.formatTimeInterval(now, getTableLoadIntervalMinutes(tableName));
  }


  public int intervalSequenceNo(String tableName, LocalDateTime snapshotTime) {
    return TimeIntervalCorrector.intervalSequenceNo(snapshotTime, getTableLoadIntervalMinutes(tableName));
  }

  public boolean shouldPutDirect(String tableName, LocalDateTime taskExecuteTime) {
    TableScheduleProperties tableProperties = ScheduledTableRegistry.getTableProperties(tableName);
    if (!tableProperties.getIsDelay()) {
      return true;
    }
    return TimeIntervalCorrector.belongInterval(taskExecuteTime, tableProperties.getLoadInterval());
  }

  public int getTableLoadIntervalMinutes(String tableName) {
    return ScheduledTableRegistry.getTableLoadIntervalMinutes(tableName);
  }

  public boolean isDelayTable(String tableName) {
    return ScheduledTableRegistry.getTableProperties(tableName).getIsDelay();
  }
}
