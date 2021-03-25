package xyz.mydev.msg.common.util;

import java.time.LocalDateTime;

public interface TimeCalculator {


  /**
   * 格式化时间 间隔5 10 15 30分钟
   *
   * @param now
   * @return
   */
  static LocalDateTime formatTimeIntervals(LocalDateTime now, int intervals) {
    int minute = now.getMinute();
    return formatTime(minute, intervals, now);
  }

  /**
   * 格式化时间 间隔5分钟
   *
   * @param now
   * @return
   */
  static LocalDateTime formatTimeFiveIntervals(LocalDateTime now) {
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
  static LocalDateTime formatTimeTenIntervals(LocalDateTime now) {
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
  static LocalDateTime formatTimeFifteenIntervals(LocalDateTime now) {
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
  static LocalDateTime formatTimeThirtyIntervals(LocalDateTime now) {
    final int intervals = 30;
    int minute = now.getMinute();
    return formatTime(minute, intervals, now);
  }


  static LocalDateTime formatTime(int minute, int intervals, LocalDateTime now) {
    int resultMinute = minute / intervals * intervals;
    return now.withMinute(resultMinute).withSecond(0).withNano(0);
  }

  /**
   * 格式化时间的 当天的序号
   *
   * @param formatTime
   * @param intervals
   * @return 序号值
   */
  static int calculateFormatTimeNum(LocalDateTime formatTime, int intervals) {
    final int minutes = 60;
    int totalMinutes = formatTime.getHour() * minutes + formatTime.getMinute();
    return totalMinutes / intervals;
  }


  /**
   * 根据当前时刻判断进来的记录是否实时加载？
   * 当前记录的时间超过当前时间，交由定时加载
   * 当前记录的时间早于当前时间，直接加载
   * 当前时刻 2:25，
   * 1、进来一条2:31的记录，应该交由定时加载而不是实时。
   * 2、进来一条2:22的记录，应该直接加载。
   * (2:00,2:30]的记录即时加载，后的定时任务加载。
   *
   * @param taskExecuteTime 目标任务记录执行时间
   * @param intervals       时间间隔
   * @return 是否需要立即调度
   */

  static boolean shouldPutDirect(LocalDateTime taskExecuteTime, int intervals) {

    final long intervalSeconds = intervalFive(intervals);
    LocalDateTime now = LocalDateTime.now();
    if (!taskExecuteTime.isAfter(now)) {
      return true;
    } else {
      LocalDateTime startTime = formatTimeIntervals(now, intervals);
      LocalDateTime endTime = startTime.plusSeconds(intervalSeconds);
      return !taskExecuteTime.isAfter(endTime);
    }
  }

  /**
   * 时间间隔秒数
   *
   * @param intervals 间隔
   * @return 间隔秒数
   */
  static long intervalFive(int intervals) {
    final int fiveIntervals = 5;
    final int tenIntervals = 10;
    final int fifteenIntervals = 15;
    final int thirtyIntervals = 30;
    if (intervals == fiveIntervals) {
      return 300;
    } else if (intervals == tenIntervals) {
      return 600;
    } else if (intervals == fifteenIntervals) {
      return 900;
    } else if (intervals == thirtyIntervals) {
      return 1800;
    }
    return 0;
  }

  /**
   * 5分钟调度间隔，单位是秒
   *
   * @return 间隔秒数
   */
  static long intervalFive() {
    return 300;
  }

  /**
   * 10分钟调度间隔，单位是秒
   *
   * @return 间隔秒数
   */
  static long intervalTen() {
    return 600;
  }

  /**
   * 15分钟调度间隔，单位是秒
   *
   * @return 间隔秒数
   */
  static long intervalFifteen() {
    return 900;
  }

  /**
   * 30分钟调度间隔，单位是秒
   *
   * @return 间隔秒数
   */
  static long intervalThirty() {
    return 1800;
  }

}
