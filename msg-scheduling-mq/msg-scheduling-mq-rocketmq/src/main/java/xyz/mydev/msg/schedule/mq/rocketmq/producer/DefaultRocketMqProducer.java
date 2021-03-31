package xyz.mydev.msg.schedule.mq.rocketmq.producer;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.springframework.beans.factory.InitializingBean;
import xyz.mydev.msg.common.Constants;
import xyz.mydev.msg.schedule.mq.producer.MqProducer;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ZSP
 */
@Slf4j
public class DefaultRocketMqProducer implements MqProducer, InitializingBean {


  private final RocketMQProperties rocketMQProperties;

  @Getter
  private TransactionMQProducer producer;
  private final TransactionMessageListenerImpl delayMessageTransactionListener;


  private DefaultRocketMqProducer(TransactionMessageListenerImpl delayMessageTransactionListener,
                                  RocketMQProperties rocketMqProperties) {

    this.delayMessageTransactionListener = delayMessageTransactionListener;
    this.rocketMQProperties = rocketMqProperties;
  }

  public TransactionSendResult sendMessage(Message message, Object argument) {
    TransactionSendResult sendResult = null;
    try {
      sendResult = this.producer.sendMessageInTransaction(message, argument);
    } catch (Exception e) {
      log.error("delay meg send error: {} ,error info: [{}]", message, e);
    }

    log.info("delay msg send result: [{}] ", sendResult);

    return sendResult;
  }


  @Override
  public TransactionSendResult sendMessage(xyz.mydev.msg.schedule.bean.Message msg) {
    if (log.isDebugEnabled()) {
      log.debug("sending msg: {}  ", msg);
    }
    String msgId = msg.getId();
    byte[] body = msg.getPayload().getBytes(StandardCharsets.UTF_8);

    Message message = new Message(msg.getTopic(), msg.getTag(), msgId, body);
    message.putUserProperty(Constants.MsgPropertiesKey.TABLE_NAME, msg.getTargetTableName());
    message.putUserProperty(Constants.MsgPropertiesKey.BUSINESS_ID, msg.getBusinessId());
    message.putUserProperty(Constants.MsgPropertiesKey.BUSINESS_ID, msg.getBusinessId());
    message.putUserProperty(Constants.MsgPropertiesKey.TRACE_ID, msg.getTraceId());
    message.putUserProperty(Constants.MsgPropertiesKey.TRACE_VERSION, msg.getTraceVersion());
    return sendMessage(message, null);
  }


  @Override
  public void afterPropertiesSet() throws Exception {
    this.producer = new TransactionMQProducer(rocketMQProperties.getProducer().getGroup());
    this.producer.setTransactionListener(delayMessageTransactionListener);
    this.producer.setNamesrvAddr(rocketMQProperties.getNameServer());
  }

  private void startProducer() throws MQClientException {
    int count = Math.min(Runtime.getRuntime().availableProcessors() / 2, 4);
    ExecutorService executorService =
      new ThreadPoolExecutor(count, count + 1, 30, TimeUnit.MINUTES,
        new ArrayBlockingQueue<>(2000),
        r -> {
          Thread thread = new Thread(r);
          thread.setName(rocketMQProperties.getProducer().getGroup() + "-ct");
          return thread;
        });
    this.producer.setExecutorService(executorService);
    producer.start();
  }

  public void start() throws Exception {
    startProducer();
  }
}
