package xyz.mydev.msg.schedule.core.config.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import xyz.mydev.msg.schedule.properties.SchedulerProperties;

/**
 * TODO spring内置的获取配置文件内容的类
 *
 * @author ZSP
 */
@ActiveProfiles("msg-schedule")
@SpringBootTest
class SchedulerPropertiesConfigurationTest {

  @Autowired
  private SchedulerProperties schedulerProperties;


  @Test
  void schedulerProperties() {
    System.out.println(schedulerProperties);
  }


}