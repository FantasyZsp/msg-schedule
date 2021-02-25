package xyz.mydev.msg.schedule.load;

import xyz.mydev.msg.schedule.bean.Message;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息加载器
 * TODO 考虑是否要将延时和事务消息调度进行统一
 *
 * @author ZSP
 */
public abstract class AbstractMessageLoader<T extends Message> implements MessageLoader<T> {

  private final MessageRepositoryRouter<T> messageRepositoryRouter;

  protected AbstractMessageLoader(MessageRepositoryRouter<T> messageRepositoryRouter) {
    this.messageRepositoryRouter = messageRepositoryRouter;
  }


  @Override
  public List<T> load(String targetTableName, LocalDateTime startTime, LocalDateTime endTime) {
    MessageRepository<T> messageRepository = messageRepositoryRouter.get(targetTableName);
    return messageRepository.findWillSendBetween(startTime, endTime);
  }
}
