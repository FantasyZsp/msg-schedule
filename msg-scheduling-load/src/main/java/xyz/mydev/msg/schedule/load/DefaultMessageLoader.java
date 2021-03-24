package xyz.mydev.msg.schedule.load;

import org.redisson.api.RedissonClient;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * @author ZSP
 */
public class DefaultMessageLoader<T extends StringMessage> implements MessageLoader<T> {

  private final MessageRepositoryRouter<T> messageRepositoryRouter;
  private final RedissonClient redissonClient;

  protected DefaultMessageLoader(MessageRepositoryRouter<T> messageRepositoryRouter,
                                 RedissonClient redissonClient) {
    this.messageRepositoryRouter = messageRepositoryRouter;
    this.redissonClient = redissonClient;
  }

  @Override
  public List<T> load(String targetTableName, LocalDateTime startTime, LocalDateTime endTime) {
    MessageRepository<T> messageRepository = messageRepositoryRouter.get(targetTableName);
    return messageRepository.findWillSendBetween(startTime, endTime);
  }

  @Override
  public Lock getScheduleLock(String tablaNameWithScheduleEndTime) {
    return redissonClient.getLock(tablaNameWithScheduleEndTime);
  }
}