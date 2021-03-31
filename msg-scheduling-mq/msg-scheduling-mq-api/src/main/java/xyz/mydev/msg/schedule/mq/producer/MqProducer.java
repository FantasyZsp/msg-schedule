package xyz.mydev.msg.schedule.mq.producer;

import xyz.mydev.msg.schedule.bean.Message;

/**
 * @author ZSP
 */
public interface MqProducer {

  Object sendMessage(Message message);

}
