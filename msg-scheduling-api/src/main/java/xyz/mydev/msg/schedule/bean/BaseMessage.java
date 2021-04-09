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

  void setId(String id);

  String getTopic();

  void setTopic(String topic);

  T getPayload();

  void setPayload(T payload);

  Integer getStatus();

  void setStatus(Integer status);

  /**
   * 隶属的表名
   */
  String getTargetTableName();
}
