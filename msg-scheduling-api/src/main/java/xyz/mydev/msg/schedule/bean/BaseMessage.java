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

  /**
   * 快照版本，一般用于写幂等
   */
  default int getSnapShotVersion() {
    return 0;
  }

  /**
   * 消息设计版本
   */
  default int getVersion() {
    return 0;
  }
}
