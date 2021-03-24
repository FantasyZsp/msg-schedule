package xyz.mydev.msg.common;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author ZSP
 */
public interface MessageDelay extends Delayed {

  /**
   * 生效时间
   *
   * @return 消息生效的时间点
   */
  LocalDateTime getTime();

  /**
   * 计算返回距离生效点的时间
   *
   * @param unit 时间单位
   * @return unit
   */
  @Override
  default long getDelay(TimeUnit unit) {
    long delay = unit.convert(Duration.between(LocalDateTime.now(), getTime()));
    return delay > 0 ? delay : 0;
  }

  /**
   * 比大小，用作排序
   *
   * @param o 比对的消息
   * @return @see {@link java.lang.Comparable#compareTo(java.lang.Object)}
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  default int compareTo(Delayed o) {
    if (o instanceof MessageDelay) {
      // 在比较时产生更少的LocalDateTime实例，缺陷是没有用给定的精度。如果需要指定精度进行排序，请覆盖此默认实现。
      return getTime().compareTo(((MessageDelay) o).getTime());
    }
    return (int) (getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
  }
}
