package xyz.mydev.msg.schedule.core.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import xyz.mydev.msg.schedule.MessageStoreEvent;
import xyz.mydev.msg.schedule.MessageStoreEventPublisher;

/**
 * @author ZSP
 */
public class DefaultMessageStoreEventPublisher implements MessageStoreEventPublisher, ApplicationEventPublisherAware {

  private ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void publishEvent(MessageStoreEvent<?> event) {
    applicationEventPublisher.publishEvent(event);
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }
}
