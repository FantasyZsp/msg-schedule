package xyz.mydev.msg.schedule.infrastruction.repository.route;

import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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

  @Override
  public Set<String> getScheduledTables() {
    return repositoryMap.keySet();
  }

  @Override
  public void init() {
    if (!supportStream()) {
      if (repositoryMap.isEmpty()) {
        throw new IllegalStateException("not found MessageRepository implements");
      }
    }
  }

  @Override
  public String toString() {
    return "DefaultMessageRepositoryRouter{" +
      "ScheduledTables=" + repositoryMap.keySet() +
      '}';
  }

  @Override
  public Iterator<MessageRepository<?>> iterator() {
    return repositoryMap.values().iterator();
  }
}
