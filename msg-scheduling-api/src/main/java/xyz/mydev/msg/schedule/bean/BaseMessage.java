package xyz.mydev.msg.schedule.bean;

import xyz.mydev.msg.common.MessageType;

/**
 * 基础消息
 * 关注投递元信息和状态信息
 *
 * @author ZSP
 */
public interface BaseMessage<T> extends MessageType {

  String getId();

  String getTopic();

  T getPayload();

  Integer getStatus();

  String getTargetTableName();

  /**
   * 快照版本，一般用于写幂等
   */
  default Long getVersion() {
    return 0L;
  }

  /**
   * 消息设计版本，兼容不同版本消息使用。
   */
  default Integer getDesignedVersion() {
    return 0;
  }
}
