package xyz.mydev.msg.schedule.infrastruction.repository.route;

import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;

/**
 * @author ZSP
 */
public interface MessageRepositoryRouter {

  <T extends StringMessage> MessageRepository<T> get(String msgTableName, Class<T> targetClass);

  <T extends StringMessage> MessageRepository<T> get(String msgTableName);


  @SuppressWarnings("unchecked")
  default <T extends StringMessage> MessageRepository<T> resolveByMessage(T msg) {
    return (MessageRepository<T>) get(msg.getTargetTableName(), msg.getClass());
  }


}
