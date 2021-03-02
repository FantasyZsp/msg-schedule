package xyz.mydev.msg.schedule;

import xyz.mydev.msg.schedule.bean.BaseMessage;

/**
 * @author ZSP
 */
public interface LocalMessageStoreEventListener<E, T extends BaseMessage<E>> {
  void onEvent(LocalMessageStoreEvent<T> localMessageStoreEvent);
}