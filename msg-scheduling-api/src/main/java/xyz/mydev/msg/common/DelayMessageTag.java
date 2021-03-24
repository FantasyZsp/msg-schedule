package xyz.mydev.msg.common;

/**
 * @author ZSP
 */
public interface DelayMessageTag extends MessageDelay, MessageTag {
  /**
   * 针对rocketmq存在，兼容rocketmq对消息的子分类
   *
   * @return 返回消息tag
   */
  @Override
  default String getTag() {
    return "*";
  }
}
