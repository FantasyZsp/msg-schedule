package xyz.mydev.msg.schedule.mq.producer;

import xyz.mydev.msg.schedule.bean.Message;

/**
 * @author ZSP
 */
public interface MqProducer {

  /**
   * 发送事务消息，需保证本地消息状态和消息发送成功的一致性
   *
   * @param message 被发送的消息。
   * @return 发送的结果。调用方可以根据发送结果做业务判断
   */
  Object sendWithTx(Message message);

  void start();

  void shutdown();

}
