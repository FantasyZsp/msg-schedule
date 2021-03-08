package xyz.mydev.msg.schedule.load;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 调度时间计算器
 *
 * @author zhaosp
 */
public interface ScheduleTimeEvaluator {
  /**
   * 让时间停在整点，防止调度误差引起的遗漏记录
   * 校准时间到整30分钟，向前  2:05 --> 2:00
   *
   * @param now 当前时间，需要被校准
   * @return 校准后时间
   */
  default LocalDateTime formatTimeWithHalfHour(LocalDateTime now) {

    final int halfHour = 30;

    LocalTime localTime = now.toLocalTime();

    LocalTime formattedTime;

    int minute = localTime.getMinute();
    if (minute >= halfHour) {
      formattedTime = localTime.withMinute(halfHour).withSecond(0).withNano(0);
    } else {
      formattedTime = localTime.withMinute(0).withSecond(0).withNano(0);
    }
    return now.with(formattedTime);
  }

  default LocalDateTime formatTimeWithDefaultInterval(LocalDateTime now) {
    return formatTimeWithHalfHour(now);
  }

  /**
   * 当前时刻 2:25，
   * 1、进来一条2:31的记录，应该交由定时加载而不是实时。
   * 2、进来一条2:22的记录，应该直接加载。
   * (2:00,2:30]的记录即时加载，后的定时任务加载。
   *
   * @param taskExecuteTime 目标任务执行时间
   * @return 是否需要立即调度
   * @apiNote 和scheduleLoader一起也可能造成数据重复，但已经屏蔽了绝大部分的重复数据。个别临界状态的，如存储到msgDb后符合direct条件同时schedule调度出现延时扫描不及时，也会扫到同样的记录，但重复的范围已经大大缩小。
   * 剩余的重复，需要依赖缓存去重。
   */
  default boolean shouldPutDirect(LocalDateTime taskExecuteTime) {

    final long intervalSeconds = intervalSeconds();
    LocalDateTime now = LocalDateTime.now();
    // 比现在还早，立即加载
    if (!taskExecuteTime.isAfter(now)) {
      return true;
    } else {
      // 处于当前时间的格式化区间内
      LocalDateTime startTime = formatTimeWithDefaultInterval(now);
      LocalDateTime endTime = startTime.plusSeconds(intervalSeconds);
      return !taskExecuteTime.isAfter(endTime);
    }
  }

  /**
   * 调度间隔，单位是秒
   */
  default long intervalSeconds() {
    return 1800;
  }

}
