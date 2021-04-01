package xyz.mydev.msg.schedule.infrastruction.repository.route;

import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;

import java.util.Set;

/**
 * @author ZSP
 */
public interface MessageRepositoryRouter extends Iterable<MessageRepository<?>> {

  <T extends StringMessage> MessageRepository<T> get(String msgTableName);

  default <T extends StringMessage> MessageRepository<T> resolveByMessage(T msg) {
    return get(msg.getTargetTableName());
  }

  <T extends StringMessage> void put(String tableName, MessageRepository<T> messageRepository);

  Set<String> getScheduledTables();

}
