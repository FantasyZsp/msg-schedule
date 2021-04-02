package xyz.mydev.msg.schedule.core.event;

import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;
import xyz.mydev.msg.schedule.MessageStoreEvent;
import xyz.mydev.msg.schedule.MessageStoreEventPublisher;

/**
 * @author ZSP
 */
public class DefaultMessageStoreEventPublisher implements MessageStoreEventPublisher {

  @Setter
  private ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void publishEvent(MessageStoreEvent<?> event) {
    applicationEventPublisher.publishEvent(event);
  }
}
