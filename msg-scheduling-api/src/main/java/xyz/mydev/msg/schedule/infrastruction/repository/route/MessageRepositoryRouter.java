package xyz.mydev.msg.schedule.infrastruction.repository.route;

import xyz.mydev.msg.common.route.Router;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;

/**
 * @author ZSP
 */
public interface MessageRepositoryRouter extends Router<String, MessageRepository> {
  @Override
  MessageRepository get(String msgTableName);
}
