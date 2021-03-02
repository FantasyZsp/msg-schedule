package xyz.mydev.msg.schedule.core.store;

import xyz.mydev.msg.schedule.LocalMessageStoreEventPublisher;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.core.event.GenericLocalMessageStoreEvent;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;

import java.util.Objects;

/**
 * @author ZSP
 */
public abstract class AbstractMessageStoreService {

  private final LocalMessageStoreEventPublisher publisher;
  private final MessageRepositoryRouter<? super StringMessage> messageRepositoryRouter;


  public AbstractMessageStoreService(LocalMessageStoreEventPublisher publisher,
                                     MessageRepositoryRouter<? super StringMessage> messageRepositoryRouter) {
    this.publisher = publisher;
    this.messageRepositoryRouter = messageRepositoryRouter;
  }

  public <T extends StringMessage> void store(T message) {
    Objects.requireNonNull(message.getId());
    messageRepositoryRouter.resolveByMessage(message).insert(message);
    publisher.publishEvent(new GenericLocalMessageStoreEvent<>(message));
  }
}
