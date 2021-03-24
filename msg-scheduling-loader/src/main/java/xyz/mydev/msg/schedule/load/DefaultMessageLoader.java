package xyz.mydev.msg.schedule.load;

import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultMessageLoader<T extends StringMessage> implements MessageLoader<T> {

  private final MessageRepositoryRouter<T> messageRepositoryRouter;
  private final Lock lock;

  protected DefaultMessageLoader(MessageRepositoryRouter<T> messageRepositoryRouter) {
    this.messageRepositoryRouter = messageRepositoryRouter;
    this.lock = new ReentrantLock();
  }

  public DefaultMessageLoader(MessageRepositoryRouter<T> messageRepositoryRouter,
                              Lock lock) {
    this.messageRepositoryRouter = messageRepositoryRouter;
    this.lock = lock;
  }

  @Override
  public List<T> load(String targetTableName, LocalDateTime startTime, LocalDateTime endTime) {
    MessageRepository<T> messageRepository = messageRepositoryRouter.get(targetTableName);
    return messageRepository.findWillSendBetween(startTime, endTime);
  }

  @Override
  public Lock getScheduleLock(String targetTableName) {
    return lock;
  }
}