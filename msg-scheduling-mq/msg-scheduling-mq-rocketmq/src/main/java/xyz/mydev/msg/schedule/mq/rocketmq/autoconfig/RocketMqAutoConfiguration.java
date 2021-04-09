package xyz.mydev.msg.schedule.mq.rocketmq.autoconfig;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import xyz.mydev.msg.schedule.IdGenerator;
import xyz.mydev.msg.schedule.mq.error.record.ErrorMessage;
import xyz.mydev.msg.schedule.mq.error.record.ErrorMessageRepository;
import xyz.mydev.msg.schedule.mq.error.record.InternalIdGenerator;
import xyz.mydev.msg.schedule.mq.producer.MqProducer;
import xyz.mydev.msg.schedule.mq.rocketmq.producer.DefaultRocketMqProducer;
import xyz.mydev.msg.schedule.mq.rocketmq.producer.TransactionMessageListenerImpl;

/**
 * @author ZSP
 */
@Configuration
@AutoConfigureAfter(RocketMQAutoConfiguration.class)
@ConditionalOnBean({RocketMQProperties.class})
@Import(ComponentScanConfig.class)
public class RocketMqAutoConfiguration {

  private static final Logger log = LoggerFactory.getLogger(RocketMqAutoConfiguration.class);

  final RocketMQProperties rocketMQProperties;

  public RocketMqAutoConfiguration(RocketMQProperties rocketMqProperties) {
    this.rocketMQProperties = rocketMqProperties;
  }

  @Bean(initMethod = "start", destroyMethod = "shutdown")
  @ConditionalOnMissingBean(MqProducer.class)
  public MqProducer mqProducer(TransactionMessageListenerImpl transactionMessageListener, DefaultMQProducer defaultMQProducer) {
    return new DefaultRocketMqProducer(transactionMessageListener, defaultMQProducer, rocketMQProperties);
  }

  @Bean
  @ConditionalOnMissingBean
  public IdGenerator idGenerator() {
    log.warn("Bad way to use InternalIdGenerator!!!");
    return new InternalIdGenerator();
  }

  @Bean
  @ConditionalOnMissingBean
  public ErrorMessageRepository mqMessageErrorRecordRepository() {
    log.warn("no msg will be stored, please implements actual MqMessageErrorRecordRepository to use");
    return new ErrorMessageRepository() {
      @Override
      public int insert(ErrorMessage record) {
        log.warn("no msg stored, please implements actual MqMessageErrorRecordRepository to use");
        return 1;
      }
    };
  }
}
