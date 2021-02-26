package xyz.mydev.msg.common;

/**
 * @author ZSP
 */
public interface MessageType {
  /**
   * 延时类消息的调度一般需要经过延时队列中间件完成。
   */
  Boolean isDelay();

  /**
   * 事务消息，必须保证100%可达，实现手段不限，参考各类MQ的实现模型即可。
   * 非事务消息，即允许一定的消息丢失，此时可选用高性能处理方式。
   */
  Boolean isTx();
}
