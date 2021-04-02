package xyz.mydev.msg.schedule;

/**
 * TODO 考虑事件包裹泛型消息的设计
 *
 * @author ZSP
 */
public interface MessageStoreEventListener<T> {
  void onEvent(T message);
}