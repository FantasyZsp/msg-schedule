package xyz.mydev.msg.schedule.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZSP
 */
@Configuration
public class SchedulerPropertiesConfiguration {

  @Bean
  @ConfigurationProperties(prefix = "msg-schedule.scheduler")
  public SchedulerProperties schedulerProperties() {
    return new SchedulerProperties();
  }

}
