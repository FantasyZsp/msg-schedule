package xyz.mydev.msg.schedule.core.store;

import xyz.mydev.msg.schedule.MessageStoreEventPublisher;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.core.event.GenericMessageStoreEvent;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;

import java.util.Objects;

/**
 * @author ZSP
 */
public class MessageStoreService {

  private final MessageStoreEventPublisher publisher;
  private final MessageRepositoryRouter messageRepositoryRouter;


  public MessageStoreService(MessageStoreEventPublisher publisher,
                             MessageRepositoryRouter messageRepositoryRouter) {
    this.publisher = publisher;
    this.messageRepositoryRouter = messageRepositoryRouter;
  }

  public <T extends StringMessage> void store(T message) {
    Objects.requireNonNull(message.getId());
    messageRepositoryRouter.resolveByMessage(message).insert(message);
    publisher.publishEvent(new GenericMessageStoreEvent<>(message));
  }
}
