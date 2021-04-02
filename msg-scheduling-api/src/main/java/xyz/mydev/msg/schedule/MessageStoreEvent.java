package xyz.mydev.msg.schedule;

/**
 * @author ZSP
 */
public interface MessageStoreEvent<T> {
  T getMessage();
}
