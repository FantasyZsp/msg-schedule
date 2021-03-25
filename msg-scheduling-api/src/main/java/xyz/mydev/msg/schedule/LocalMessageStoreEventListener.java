package xyz.mydev.msg.schedule;

/**
 * @author ZSP
 */
public interface LocalMessageStoreEventListener<E, T> {
  void onEvent(LocalMessageStoreEvent<T> localMessageStoreEvent);
}