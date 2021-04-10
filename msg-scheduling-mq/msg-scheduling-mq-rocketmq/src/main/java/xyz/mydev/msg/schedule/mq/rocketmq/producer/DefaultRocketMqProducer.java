package xyz.mydev.msg.schedule.mq.rocketmq.producer;


import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import xyz.mydev.msg.common.Constants;
import xyz.mydev.msg.schedule.mq.producer.MqProducer;
import xyz.mydev.msg.schedule.mq.rocketmq.RocketMQUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ZSP
 */
public class DefaultRocketMqProducer implements MqProducer, InitializingBean {

  private final RocketMQProperties rocketMQProperties;
  private final TransactionMQProducer producer;


  public DefaultRocketMqProducer(TransactionMessageListenerImpl transactionMessageListener,
                                 DefaultMQProducer defaultMQProducer,
                                 RocketMQProperties rocketMqProperties) {
    this.rocketMQProperties = rocketMqProperties;

    this.producer = (TransactionMQProducer) defaultMQProducer;
    this.producer.setTransactionListener(transactionMessageListener);
  }

  private final static Logger log = LoggerFactory.getLogger(DefaultRocketMqProducer.class);

  private TransactionSendResult sendMessage(Message message) {
    TransactionSendResult sendResult = null;
    try {
      sendResult = this.producer.sendMessageInTransaction(message, message.getProperty(Constants.MsgPropertiesKey.BUSINESS_ID));
    } catch (Exception e) {
      log.error("msg send error: {} ,error info:", message, e);
    }

    if (log.isDebugEnabled()) {
      log.debug("msg send result: [{}] ", sendResult);
    }

    return sendResult;
  }


  @Override
  public TransactionSendResult sendWithTx(xyz.mydev.msg.schedule.bean.Message msg) {
    if (log.isDebugEnabled()) {
      log.debug("sending msg: {}  ", msg);
    }
    String msgId = msg.getId();
    byte[] body = msg.getPayload().getBytes(StandardCharsets.UTF_8);

    // TODO fix 空字符
    Message message = new Message(msg.getTopic(), msg.getTag(), msgId, body);
    RocketMQUtils.putValueIfNotNull(message, Constants.MsgPropertiesKey.TABLE_NAME, msg.getTargetTableName());
    RocketMQUtils.putValueIfNotNull(message, Constants.MsgPropertiesKey.BUSINESS_ID, msg.getBusinessId());
    RocketMQUtils.putValueIfNotNull(message, Constants.MsgPropertiesKey.TRACE_ID, msg.getTraceId());
    RocketMQUtils.putValueIfNotNull(message, Constants.MsgPropertiesKey.TRACE_VERSION, msg.getTraceVersion());

    return sendMessage(message);
  }


  @Override
  public void afterPropertiesSet() throws Exception {
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
    // do not start producer because rocketMQTemplate will start it
  }

  @Override
  public void start() {
    log.info("DefaultRocketMqProducer starting...");
    try {
      startProducer();
    } catch (MQClientException e) {
      throw new RuntimeException(e);
    }
    log.info("DefaultRocketMqProducer started");

  }
}
