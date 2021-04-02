package xyz.mydev.msg.schedule;

/**
 * @author ZSP
 */
public interface MessageStoreEventPublisher {

  void publishEvent(MessageStoreEvent<?> event);

}
