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
   */
  LocalDateTime getTime();

  @Override
  default long getDelay(TimeUnit unit) {
    long delay = unit.convert(Duration.between(LocalDateTime.now(), getTime()));
    return delay > 0 ? delay : 0;
  }

  @Override
  default int compareTo(Delayed o) {
    if (o instanceof MessageDelay) {
      // 在比较时产生更少的LocalDateTime实例，缺陷是没有用给定的精度。如果需要指定精度进行排序，请覆盖此默认实现。
      return getTime().compareTo(((MessageDelay) o).getTime());
    }
    return (int) (getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
  }
}
