package xyz.mydev.msg.schedule.example.delay.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.mydev.msg.common.util.JsonUtil;
import xyz.mydev.msg.schedule.IdGenerator;
import xyz.mydev.msg.schedule.mq.error.record.InternalIdGenerator;

/**
 * @author ZSP
 */
@Slf4j
@Configuration
public class BootConfig {

  @Bean
  @ConditionalOnMissingBean
  public IdGenerator idGenerator() {
    log.warn("BootConfig Bad way to use InternalIdGenerator");
    return new InternalIdGenerator();
  }

  @Bean
  @ConditionalOnMissingBean
  public Config redissonSingleServerConfig() {
    Config config = new Config();
    config.setCodec(new JsonJacksonCodec(JsonUtil.objectMapper.copy()));
    return config;
  }
}
