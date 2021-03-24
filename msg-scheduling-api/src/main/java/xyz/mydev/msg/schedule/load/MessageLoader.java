package xyz.mydev.msg.schedule.load;

import xyz.mydev.msg.schedule.bean.StringMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;

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
   * @return 需要调度投递的消息，一般消息表中会维护消息状态。
   */
  List<T> load(String targetTableName, LocalDateTime startTime, LocalDateTime endTime);

  /**
   * 获取一把调度锁
   * 默认根据被调度的表表名即可控制，考虑到对调度时段的分离，尤其是启动任务和定时任务的冲突，实现者可以在表名基础上追加当前调度的时间批次，从而允许不同时段的任务并发。
   *
   * @param targetTableName 目标表
   * @return 调度锁，用于控制调度与读写并发
   */
  Lock getScheduleLock(String targetTableName);


}
