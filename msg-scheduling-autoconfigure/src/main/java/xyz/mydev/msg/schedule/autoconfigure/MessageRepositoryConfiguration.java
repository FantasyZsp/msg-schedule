package xyz.mydev.msg.schedule.autoconfigure;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;
import xyz.mydev.msg.schedule.infrastruction.repository.route.DefaultMessageRepositoryRouter;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;

/**
 * @author ZSP
 */
@Configuration
public class MessageRepositoryConfiguration {

  private final ObjectProvider<MessageRepository<? extends StringMessage>> provider;

  public MessageRepositoryConfiguration(ObjectProvider<MessageRepository<? extends StringMessage>> provider) {
    this.provider = provider;
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageRepositoryRouter messageRepositoryRouter() {
    DefaultMessageRepositoryRouter router = new DefaultMessageRepositoryRouter();
    provider.ifAvailable(repository -> router.put(repository.getTableName(), repository));
//    router.init();
    return router;
  }

}
