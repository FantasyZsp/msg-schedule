package xyz.mydev.msg.schedule.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import xyz.mydev.msg.schedule.autoconfigure.properties.SchedulerProperties;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;
import xyz.mydev.msg.schedule.load.MessageLoader;
import xyz.mydev.msg.schedule.mq.producer.MqProducer;
import xyz.mydev.msg.schedule.port.route.PorterRouter;
import xyz.mydev.redis.lock.util.ThreadUtils;

/**
 * @author ZSP
 */
@ActiveProfiles("msg-schedule")
@SpringBootTest
class SchedulerPropertiesConfigurationTest {

  @Autowired
  private SchedulerProperties schedulerProperties;
  @Autowired
  private PorterRouter porterRouter;
  @Autowired
  private MessageLoader messageLoader;

  @Autowired
  private MessageRepositoryRouter messageRepositoryRouter;
  @Autowired
  private MqProducer mqProducer;
  @Autowired
  private ApplicationContext applicationContext;


  @Test
  void schedulerProperties() {
    System.out.println(schedulerProperties);
    ThreadUtils.sleepSeconds(500);
  }

  @Test
  void testClass() {
  }

}