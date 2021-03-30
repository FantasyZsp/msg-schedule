package xyz.mydev.msg.schedule.bean;

/**
 * @author ZSP
 */

public interface InstantMessage extends Message {
  @Override
  default Boolean isDelay() {
    return false;
  }
}
