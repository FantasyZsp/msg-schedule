package xyz.mydev.msg.schedule;

/**
 * @author ZSP
 */
public interface MessageStoreEventListener<T> {
  void onEvent(MessageStoreEvent<T> messageStoreEvent);
}