package xyz.mydev.msg.schedule.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.mydev.msg.schedule.properties.SchedulerProperties;

/**
 * @author ZSP
 */
@Configuration
public class SchedulerPropertiesConfiguration {

  @Bean(initMethod = "init")
  @ConfigurationProperties(prefix = "msg-schedule.scheduler")
  @ConditionalOnProperty(value = "msg-schedule.scheduler.enable", havingValue = "true")
  public SchedulerProperties schedulerProperties() {
    return new SchedulerProperties();
  }

}
