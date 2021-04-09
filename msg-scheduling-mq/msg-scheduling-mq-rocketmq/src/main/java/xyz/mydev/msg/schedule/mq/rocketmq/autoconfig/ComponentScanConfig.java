package xyz.mydev.msg.schedule.mq.rocketmq.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @author ZSP
 */
@Configuration
@ComponentScan(
  basePackages = {"xyz.mydev.msg.schedule.mq.error.record", "xyz.mydev.msg.schedule.mq.rocketmq.producer"},
  excludeFilters = {@ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
    @ComponentScan.Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class)})
public class ComponentScanConfig {
}
