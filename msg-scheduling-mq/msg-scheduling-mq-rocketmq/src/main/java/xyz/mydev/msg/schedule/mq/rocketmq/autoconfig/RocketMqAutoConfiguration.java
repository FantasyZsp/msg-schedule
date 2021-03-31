package xyz.mydev.msg.schedule.mq.rocketmq.autoconfig;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.mydev.msg.schedule.IdGenerator;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;
import xyz.mydev.msg.schedule.mq.error.record.InternalIdGenerator;
import xyz.mydev.msg.schedule.mq.error.record.MqMessageErrorRecordService;
import xyz.mydev.msg.schedule.mq.error.record.RocketMqMsgSendFailureHandler;
import xyz.mydev.msg.schedule.mq.producer.MqProducer;
import xyz.mydev.msg.schedule.mq.rocketmq.producer.DefaultRocketMqProducer;
import xyz.mydev.msg.schedule.mq.rocketmq.producer.TransactionMessageListenerImpl;

/**
 * @author ZSP
 */
@Configuration
@AutoConfigureAfter(RocketMQAutoConfiguration.class)
@ConditionalOnBean({RocketMQProperties.class})
@Slf4j
public class RocketMqAutoConfiguration {

  final RocketMQProperties rocketMQProperties;

  public RocketMqAutoConfiguration(RocketMQProperties rocketMqProperties) {
    this.rocketMQProperties = rocketMqProperties;
  }

  @Bean
  @ConditionalOnMissingBean(MqProducer.class)
  public MqProducer mqProducer(TransactionMessageListenerImpl transactionMessageListener) {
    return new DefaultRocketMqProducer(transactionMessageListener, rocketMQProperties);
  }

  @Bean
  @ConditionalOnMissingBean(TransactionMessageListenerImpl.class)
  public TransactionMessageListenerImpl transactionMessageListener(MessageRepositoryRouter messageRepositoryRouter,
                                                                   RocketMqMsgSendFailureHandler rocketMqMsgSendFailureHandler) {
    return new TransactionMessageListenerImpl(messageRepositoryRouter, rocketMqMsgSendFailureHandler);
  }

  @Bean
  @ConditionalOnMissingBean
  public RocketMqMsgSendFailureHandler rocketMqMsgSendFailureHandler(MqMessageErrorRecordService mqMessageErrorRecordService,
                                                                     IdGenerator idGenerator) {
    return new RocketMqMsgSendFailureHandler(mqMessageErrorRecordService, idGenerator);
  }

  @Bean
  @ConditionalOnMissingBean
  public IdGenerator idGenerator() {
    log.warn("Bad way to use InternalIdGenerator!!!");
    return new InternalIdGenerator();
  }


}
