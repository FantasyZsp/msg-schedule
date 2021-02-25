package xyz.mydev.msg.common;

/**
 * @author ZSP
 */
public interface DelayMessageTag extends MessageDelay, MessageTag {
  @Override
  default String getTag() {
    return "*";
  }
}
