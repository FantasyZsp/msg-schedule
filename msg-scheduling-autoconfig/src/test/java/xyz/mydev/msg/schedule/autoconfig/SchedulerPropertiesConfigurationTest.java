package xyz.mydev.msg.schedule.autoconfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import xyz.mydev.msg.schedule.CoreAppTest;
import xyz.mydev.msg.schedule.autoconfig.properties.SchedulerProperties;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;
import xyz.mydev.msg.schedule.load.MessageLoader;
import xyz.mydev.msg.schedule.port.DefaultInstantMessagePorter;
import xyz.mydev.msg.schedule.port.route.PorterRouter;

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


  @Test
  void schedulerProperties() {
    System.out.println(schedulerProperties);
  }

  @Test
  void testClass() {
    DefaultInstantMessagePorter defaultInstantMessagePorter = new DefaultInstantMessagePorter(null, CoreAppTest.PersonMessage.class, null, null);
  }
}