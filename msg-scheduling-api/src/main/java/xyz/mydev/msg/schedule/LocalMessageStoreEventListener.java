package xyz.mydev.msg.schedule;

/**
 * @author ZSP
 */
public interface LocalMessageStoreEventListener<T> {
  void onEvent(LocalMessageStoreEvent<T> localMessageStoreEvent);
}