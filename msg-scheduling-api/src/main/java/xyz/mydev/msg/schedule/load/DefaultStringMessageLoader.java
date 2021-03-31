package xyz.mydev.msg.schedule.load;

import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

/**
 * @author ZSP
 */
public class DefaultStringMessageLoader implements MessageLoader {

  private final MessageRepositoryRouter messageRepositoryRouter;
  private final Function<String, Lock> lockFunction;

  public DefaultStringMessageLoader(MessageRepositoryRouter messageRepositoryRouter,
                                    Function<String, Lock> lockFunction) {
    this.messageRepositoryRouter = messageRepositoryRouter;
    this.lockFunction = lockFunction;
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> List<T> load(String targetTableName, LocalDateTime startTime, LocalDateTime endTime) {
    MessageRepository<? extends StringMessage> messageRepository = messageRepositoryRouter.get(targetTableName);
    return (List<T>) messageRepository.findWillSendBetween(startTime, endTime);
  }

  @Override
  public Lock getScheduleLock(String tablaNameWithScheduleEndTime) {
    return lockFunction.apply(tablaNameWithScheduleEndTime);
  }

}