package xyz.mydev.msg.schedule;

import xyz.mydev.msg.common.util.TimeIntervalCorrector;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 调度时间评估器
 * 根据当前时间和任务的执行时间需求，计算对应的调度策略。
 * <p>
 * TODO 初始化时  关于 表加载时间范围的外部化配置与策略类实现
 *
 * @author zhaosp
 */
public class ScheduleTimeEvaluator {

  /**
   * tableName -> intervalMinus
   */
  private final Map<String, Integer> intervalMinutesMap = new HashMap<>();

  private int defaultIntervalMinutes = 30;


  public LocalDateTime formatTimeWithInterval(LocalDateTime now, Integer intervalMinutes) {
    return TimeIntervalCorrector.formatTimeInterval(now, intervalMinutes);
  }

  public LocalDateTime formatTimeWithTable(String tableName, LocalDateTime now) {
    return TimeIntervalCorrector.formatTimeInterval(now, getTableIntervalMinutes(tableName));
  }


  public int intervalSequenceNo(String tableName, LocalDateTime snapshotTime) {
    return TimeIntervalCorrector.intervalSequenceNo(snapshotTime, getTableIntervalMinutes(tableName));
  }

  public boolean shouldPutDirect(String tableName, LocalDateTime taskExecuteTime) {
    return TimeIntervalCorrector.belongInterval(taskExecuteTime, getTableIntervalMinutes(tableName));
  }

  public int getTableIntervalMinutes(String tableName) {
    return intervalMinutesMap.getOrDefault(tableName, defaultIntervalMinutes);
  }


  public void initIntervalMinutesMap(Map<String, Integer> source) {
    if (source != null) {
      intervalMinutesMap.putAll(source);
    }
  }

  public void setDefaultIntervalMinutes(int defaultIntervalMinutes) {
    if (TimeIntervalCorrector.validInterval(defaultIntervalMinutes)) {
      throw new IllegalArgumentException();
    }
    this.defaultIntervalMinutes = defaultIntervalMinutes;
  }
}
