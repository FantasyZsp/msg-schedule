package xyz.mydev.msg.common;

/**
 * @author ZSP
 */
public class Constants {

  public interface MessageStatus {
    int CONSUME_ERROR = -2;
    int SEND_ERROR = -1;
    int CREATED = 0;
    int SENT = 1;
    int CONSUMED = 2;
  }

  public interface MqPlatform {
    int ROCKETMQ = 1;
    int RABBITMQ = 2;
    int KAFKA = 3;
  }
}
