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
  private final TransactionMessageListenerImpl transactionMessageListener;


  public DefaultRocketMqProducer(TransactionMessageListenerImpl transactionMessageListener,
                                 RocketMQProperties rocketMqProperties) {

    this.transactionMessageListener = transactionMessageListener;
    this.rocketMQProperties = rocketMqProperties;
  }

  private TransactionSendResult sendMessage(Message message) {
    TransactionSendResult sendResult = null;
    try {
      sendResult = this.producer.sendMessageInTransaction(message, message.getProperty(Constants.MsgPropertiesKey.BUSINESS_ID));
    } catch (Exception e) {
      log.error("msg send error: {} ,error info: [{}]", message, e);
    }

    log.info("msg send result: [{}] ", sendResult);

    return sendResult;
  }


  @Override
  public TransactionSendResult sendWithTx(xyz.mydev.msg.schedule.bean.Message msg) {
    if (log.isDebugEnabled()) {
      log.debug("sending msg: {}  ", msg);
    }
    String msgId = msg.getId();
    byte[] body = msg.getPayload().getBytes(StandardCharsets.UTF_8);

    Message message = new Message(msg.getTopic(), msg.getTag(), msgId, body);
    message.putUserProperty(Constants.MsgPropertiesKey.TABLE_NAME, msg.getTargetTableName());
    message.putUserProperty(Constants.MsgPropertiesKey.BUSINESS_ID, msg.getBusinessId());
    message.putUserProperty(Constants.MsgPropertiesKey.TRACE_ID, msg.getTraceId());
    message.putUserProperty(Constants.MsgPropertiesKey.TRACE_VERSION, msg.getTraceVersion());
    return sendMessage(message);
  }


  @Override
  public void afterPropertiesSet() throws Exception {
    this.producer = new TransactionMQProducer(rocketMQProperties.getProducer().getGroup() + "-tx");
    this.producer.setTransactionListener(transactionMessageListener);
    this.producer.setNamesrvAddr(rocketMQProperties.getNameServer());
  }

  @Override
  public void shutdown() {
    producer.shutdown();
  }

  protected void startProducer() throws MQClientException {
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

  @Override
  public void start() {
    try {
      startProducer();
    } catch (MQClientException e) {
      throw new RuntimeException(e);
    }
  }
}
