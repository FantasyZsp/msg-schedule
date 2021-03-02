package xyz.mydev.msg.schedule.infrastruction.repository.route;

import xyz.mydev.msg.common.route.Router;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;

/**
 * @author ZSP
 */
public interface MessageRepositoryRouter<T extends StringMessage> extends Router<String, MessageRepository<T>> {
  @Override
  MessageRepository<T> get(String msgTableName);


  default MessageRepository<T> resolveByMessage(T msg) {
    return get(msg.getTargetTableName());
  }
}
