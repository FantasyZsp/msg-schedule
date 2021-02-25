package xyz.mydev.msg.schedule.load;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息加载器
 *
 * @author ZSP
 */
public interface MessageLoader<T> {
  /**
   * 从指定时间加载消息
   *
   * @param startTime 开始时间
   * @param endTime   结束时间
   */
  List<T> load(LocalDateTime startTime, LocalDateTime endTime);
}
