package xyz.mydev.msg.schedule.autoconfigure;

import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.mydev.msg.common.util.JsonUtil;
import xyz.mydev.redis.lock.annotation.RedissonClientAutoConfig;

/**
 * @author ZSP
 */
@Configuration
@AutoConfigureBefore({RedissonClientAutoConfig.class})
public class RedissonClientCodecConfiguration {
  @Bean
  @ConditionalOnMissingBean
  public Config redissonSingleServerConfig() {
    Config config = new Config();
    config.setCodec(new JsonJacksonCodec(JsonUtil.objectMapper.copy()));
    return config;
  }
}
