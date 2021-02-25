package xyz.mydev.msg.schedule.infrastruction.repository.route;

import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;

/**
 * @author ZSP
 */
public interface MessageRepositoryRouter {
  MessageRepository getByRouteKey(String routeKey);
}
