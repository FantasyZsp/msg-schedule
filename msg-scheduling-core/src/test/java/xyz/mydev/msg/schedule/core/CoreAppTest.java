package xyz.mydev.msg.schedule.core;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import xyz.mydev.msg.schedule.bean.Message;

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

  public static class PersonMessage implements Message {

    @Override
    public String getTraceId() {
      return null;
    }

    @Override
    public String getTraceVersion() {
      return null;
    }

    @Override
    public String getBusinessId() {
      return null;
    }

    @Override
    public int getPlatform() {
      return 0;
    }

    @Override
    public String getPlatformMsgId() {
      return null;
    }

    @Override
    public String getTag() {
      return null;
    }

    @Override
    public String getId() {
      return null;
    }

    @Override
    public String getTopic() {
      return null;
    }

    @Override
    public String getPayload() {
      return null;
    }

    @Override
    public Integer getStatus() {
      return null;
    }

    @Override
    public String getTargetTableName() {
      return null;
    }

    @Override
    public Boolean isDelay() {
      return null;
    }

    @Override
    public Boolean isTx() {
      return null;
    }
  }
}
