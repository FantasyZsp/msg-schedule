package xyz.mydev.msg.schedule.core.event;

import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;
import xyz.mydev.msg.schedule.LocalMessageStoreEvent;
import xyz.mydev.msg.schedule.LocalMessageStoreEventPublisher;

/**
 * @author ZSP
 */
public class DefaultLocalMessageStoreEventPublisher implements LocalMessageStoreEventPublisher {

  @Setter
  private ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void publishEvent(LocalMessageStoreEvent<?> event) {
    applicationEventPublisher.publishEvent(event);
  }
}
