package xyz.mydev.msg.common.util;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * @author ZSP
 */
public class TimeIntervalCorrector {

  public static final Set<Integer> SUPPORTED_INTERVAL = Set.of(0, 5, 10, 15, 20, 30);


  public static boolean validInterval(Integer intervalMinutes) {
    return intervalMinutes != null && SUPPORTED_INTERVAL.contains(intervalMinutes);
  }

  /**
   * 计算矫正时间，矫正基于固定的间隔：5 10 15 20 30分钟。
   *
   * @param snapshotTime    即时时间，需要被矫正的时间
   * @param intervalMinutes 需要的时间间隔，单位分钟
   * @return 被矫正的时间，基于间隔的整数倍时间
   */
  public static LocalDateTime formatTimeInterval(LocalDateTime snapshotTime, Integer intervalMinutes) {
    if (!SUPPORTED_INTERVAL.contains(Objects.requireNonNull(intervalMinutes))) {
      throw new IllegalArgumentException();
    }

    if (intervalMinutes.equals(0)) {
      return snapshotTime;
    }

    return snapshotTime.withMinute(snapshotTime.getMinute() / intervalMinutes * intervalMinutes).withSecond(0).withNano(0);
  }

  /**
   * 获取当前时间矫正后对应的序号
   *
   * @param snapshotTime    即时时间。计算前需要。
   * @param intervalMinutes 需要的时间间隔，单位分钟
   * @return 序号值，0点对应0。格式化时间分钟数 / intervalMinutes
   */
  public static int intervalSequenceNo(LocalDateTime snapshotTime, Integer intervalMinutes) {
    Objects.requireNonNull(intervalMinutes);
    if (intervalMinutes == 0) {
      throw new IllegalArgumentException();
    }

    if (!SUPPORTED_INTERVAL.contains(intervalMinutes)) {
      throw new IllegalArgumentException();
    }
    return (snapshotTime.getHour() * 60 + snapshotTime.getMinute()) / intervalMinutes;
  }


  public static boolean belongInterval(LocalDateTime taskExecuteTime, Integer intervalMinutes) {
    return belongInterval(taskExecuteTime, LocalDateTime.now(), intervalMinutes);
  }

  /**
   * 根据当前时刻判断 taskExecuteTime 是否落入当前调度间隔
   * 当前记录的时间超过当前时间，交由定时加载
   * 当前记录的时间早于当前时间，直接加载
   * <p>
   * 假设间隔30，当前时刻 2:25，
   * 1、进来一条2:31的记录，应该交由定时加载而不是实时。
   * 2、进来一条2:22的记录，应该直接加载。
   * [2:00,2:30]的记录即时加载，而后的定时任务加载。
   *
   * @param taskExecuteTime 目标任务记录执行时间
   * @param baseTime        判断的基准时间。当null时，使用本地时间；非null时，采用外部给出的基准时间
   * @param intervalMinutes 时间间隔，单位分钟
   * @return 是否需要立即调度
   */
  public static boolean belongInterval(LocalDateTime taskExecuteTime, LocalDateTime baseTime, Integer intervalMinutes) {
    baseTime = baseTime == null ? LocalDateTime.now() : baseTime;
    if (!taskExecuteTime.isAfter(baseTime)) {
      return true;
    } else {
      LocalDateTime startTime = formatTimeInterval(baseTime, intervalMinutes);
      LocalDateTime endTime = startTime.plusMinutes(intervalMinutes);
      return !taskExecuteTime.isAfter(endTime);
    }
  }

}
