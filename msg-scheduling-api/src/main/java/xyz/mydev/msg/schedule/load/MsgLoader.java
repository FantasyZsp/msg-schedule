package xyz.mydev.msg.schedule.load;

import java.time.LocalDateTime;

/**
 * 消息加载器
 *
 * @author ZSP
 */
public interface MsgLoader {
  /**
   * 从指定时间加载消息
   *
   * @param startTime 开始时间
   * @param endTime   结束时间
   */
  void load(LocalDateTime startTime, LocalDateTime endTime);
}
