package xyz.mydev.msg.schedule.bean;

/**
 * 基础消息
 * 关注投递元信息和状态信息
 *
 * @author ZSP
 */
public interface BaseMessage<T> {

  String getId();

  String getTopic();

  T getPayload();

  int getStatus();

  default long getVersion() {
    return 0;
  }
}
