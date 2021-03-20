package xyz.mydev.msg.schedule.load;

import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息加载器
 *
 * @author ZSP
 */
public class DefaultMessageLoader<T extends StringMessage> implements MessageLoader<T> {

  private final MessageRepositoryRouter<T> messageRepositoryRouter;

  protected DefaultMessageLoader(MessageRepositoryRouter<T> messageRepositoryRouter) {
    this.messageRepositoryRouter = messageRepositoryRouter;
  }


  @Override
  public List<T> load(String targetTableName, LocalDateTime startTime, LocalDateTime endTime) {
    MessageRepository<T> messageRepository = messageRepositoryRouter.get(targetTableName);
    return messageRepository.findWillSendBetween(startTime, endTime);
  }
}
