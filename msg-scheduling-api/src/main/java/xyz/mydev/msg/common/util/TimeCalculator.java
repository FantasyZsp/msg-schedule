package xyz.mydev.msg.common.util;

import java.time.LocalDateTime;

public interface TimeCalculator {


  /**
   * 格式化时间 间隔5 10 15 30分钟
   *
   * @param now
   * @return
   */
  default LocalDateTime formatTimeIntervals(LocalDateTime now, int intervals) {
    int minute = now.getMinute();
    return formatTime(minute, intervals, now);
  }

  /**
   * 格式化时间 间隔5分钟
   *
   * @param now
   * @return
   */
  default LocalDateTime formatTimeFiveIntervals(LocalDateTime now) {
    final int intervals = 5;
    int minute = now.getMinute();
    return formatTime(minute, intervals, now);
  }

  /**
   * 格式化时间 间隔10分钟
   *
   * @param now
   * @return
   */
  default LocalDateTime formatTimeTenIntervals(LocalDateTime now) {
    final int intervals = 10;
    int minute = now.getMinute();
    return formatTime(minute, intervals, now);
  }

  /**
   * 格式化时间 间隔15分钟
   *
   * @param now
   * @return
   */
  default LocalDateTime formatTimeFifteenIntervals(LocalDateTime now) {
    final int intervals = 15;
    int minute = now.getMinute();
    return formatTime(minute, intervals, now);
  }

  /**
   * 格式化时间 间隔30分钟
   *
   * @param now
   * @return
   */
  default LocalDateTime formatTimeThirtyIntervals(LocalDateTime now) {
    final int intervals = 30;
    int minute = now.getMinute();
    return formatTime(minute, intervals, now);
  }


  default LocalDateTime formatTime(int minute, int intervals, LocalDateTime now) {
    int resultMinute = minute / intervals * intervals;
    return now.withMinute(resultMinute).withSecond(0).withNano(0);
  }

  /**
   * 格式化时间的 当天的序号
   *
   * @param formatTime
   * @param intervals
   * @return
   */
  default int calculateFormatTimeNum(LocalDateTime formatTime, int intervals) {
    final int minutes = 60;
    int totalMinutes = formatTime.getHour() * minutes + formatTime.getMinute();
    return totalMinutes / intervals;
  }


  /**
   * 根据当前时刻判断进来的记录是否实时加载？
   *
   *
   * @param taskExecuteTime
   * @param intervals
   * @return
   */
  default boolean shouldPutDirect(LocalDateTime taskExecuteTime, int intervals) {


    LocalDateTime now = LocalDateTime.now();
    if (!taskExecuteTime.isAfter(now)) {
      return true;
    } else {
      LocalDateTime startTime = formatTimeIntervals(now, intervals);
      LocalDateTime endTime = startTime.plusSeconds(intervals);
      return !taskExecuteTime.isAfter(endTime);
    }
  }

  /**
   * 5分钟调度间隔，单位是秒
   */
  default long intervalFive() {
    return 300;
  }

  /**
   * 10分钟调度间隔，单位是秒
   */
  default long intervalTen() {
    return 600;
  }

  /**
   * 15分钟调度间隔，单位是秒
   */
  default long intervalFifteen() {
    return 1200;
  }

  /**
   * 30分钟调度间隔，单位是秒
   */
  default long intervalThirty() {
    return 1800;
  }

}
