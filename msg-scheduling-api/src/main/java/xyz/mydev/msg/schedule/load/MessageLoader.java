package xyz.mydev.msg.schedule.load;

import xyz.mydev.msg.schedule.bean.StringMessage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息加载器
 *
 * @author ZSP
 */
public interface MessageLoader<T extends StringMessage> {
  /**
   * 从指定时间加载消息
   *
   * @param targetTableName 目标表
   * @param startTime       开始时间
   * @param endTime         结束时间
   */
  List<T> load(String targetTableName, LocalDateTime startTime, LocalDateTime endTime);


}
