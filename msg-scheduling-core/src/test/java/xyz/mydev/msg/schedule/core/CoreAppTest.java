package xyz.mydev.msg.schedule.core;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.util.Objects;

/**
 * @author ZSP
 */
@SpringBootApplication
public class CoreAppTest {
  public static void main(String[] args) {
    SpringApplication.run(CoreAppTest.class, args);
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer properties() {
    PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
    YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
    yaml.setResources(new ClassPathResource("config-demo.yml"));
    configurer.setProperties(Objects.requireNonNull(yaml.getObject()));
    return configurer;
  }
}
