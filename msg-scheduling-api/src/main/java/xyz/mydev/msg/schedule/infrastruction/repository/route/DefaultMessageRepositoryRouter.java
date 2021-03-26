package xyz.mydev.msg.schedule.infrastruction.repository.route;

import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ZSP
 */
public class DefaultMessageRepositoryRouter implements MessageRepositoryRouter {

  private final Map<String, MessageRepository<?>> repositoryMap = new ConcurrentHashMap<>();

  @SuppressWarnings("unchecked")
  @Override
  public <T extends StringMessage> MessageRepository<T> get(String msgTableName) {
    return (MessageRepository<T>) repositoryMap.get(msgTableName);
  }

  @Override
  public <T extends StringMessage> MessageRepository<T> resolveByMessage(T msg) {
    return get(msg.getTargetTableName());
  }

  @Override
  public <T extends StringMessage> void put(String tableName, MessageRepository<T> messageRepository) {
    repositoryMap.put(tableName, messageRepository);
  }
}
