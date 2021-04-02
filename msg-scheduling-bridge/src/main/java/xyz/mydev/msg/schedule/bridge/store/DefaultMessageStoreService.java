package xyz.mydev.msg.schedule.bridge.store;

import xyz.mydev.msg.schedule.MessageStoreEventPublisher;
import xyz.mydev.msg.schedule.MessageStoreService;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.bridge.event.GenericMessageStoreEvent;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;

import java.util.Objects;

/**
 * @author ZSP
 */
public class DefaultMessageStoreService implements MessageStoreService {

  private final MessageStoreEventPublisher publisher;
  private final MessageRepositoryRouter messageRepositoryRouter;


  public DefaultMessageStoreService(MessageStoreEventPublisher publisher,
                                    MessageRepositoryRouter messageRepositoryRouter) {
    this.publisher = publisher;
    this.messageRepositoryRouter = messageRepositoryRouter;
  }

  @Override
  public void store(StringMessage messageEntity) {
    Objects.requireNonNull(messageEntity.getId());
    messageRepositoryRouter.resolveByMessage(messageEntity).insert(messageEntity);
    publisher.publishEvent(new GenericMessageStoreEvent<>(messageEntity));
  }
}
