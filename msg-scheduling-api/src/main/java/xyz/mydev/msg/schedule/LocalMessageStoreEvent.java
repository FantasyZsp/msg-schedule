package xyz.mydev.msg.schedule;

/**
 * @author ZSP
 */
public interface LocalMessageStoreEvent<T> {
  T getLocalMessage();
}
