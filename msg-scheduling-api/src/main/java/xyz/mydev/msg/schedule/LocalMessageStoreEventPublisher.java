package xyz.mydev.msg.schedule;

/**
 * @author ZSP
 */
public interface LocalMessageStoreEventPublisher {

  void publishEvent(LocalMessageStoreEvent<?> event);

}
