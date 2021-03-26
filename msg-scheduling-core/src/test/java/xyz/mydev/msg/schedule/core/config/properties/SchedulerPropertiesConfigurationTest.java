package xyz.mydev.msg.schedule.core.config.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.mydev.msg.schedule.properties.SchedulerProperties;

/**
 * @author ZSP
 */
@SpringBootTest
class SchedulerPropertiesConfigurationTest {

  @Autowired
  private SchedulerProperties schedulerProperties;


  @Test
  void schedulerProperties() {
    System.out.println(schedulerProperties);
  }


}