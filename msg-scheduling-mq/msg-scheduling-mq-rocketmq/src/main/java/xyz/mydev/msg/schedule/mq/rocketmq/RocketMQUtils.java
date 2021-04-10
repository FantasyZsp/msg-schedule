package xyz.mydev.msg.schedule.mq.rocketmq;

import org.apache.rocketmq.common.message.Message;

import java.util.Objects;

/**
 * @author ZSP
 */
public class RocketMQUtils {

  public static void putValueIfNotNull(Message message, String key, String value) {
    if (value != null) {
      Objects.requireNonNull(message).putUserProperty(Objects.requireNonNull(key), value);
    }
  }

}
