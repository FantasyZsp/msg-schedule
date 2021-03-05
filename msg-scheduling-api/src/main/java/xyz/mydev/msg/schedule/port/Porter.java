package xyz.mydev.msg.schedule.port;

import java.util.concurrent.Executor;

/**
 * @author ZSP
 */
public interface Porter<E> {


  Executor getPutExecutor();

  Executor getPortExecutor();

  /**
   * 直接调度msg到mq平台
   */
  void port(E msg);


  default void portAsync(E msg) {
    getPortExecutor().execute(() -> port(msg));
  }

  void put(E msg);

  /**
   * 将要投递的消息暂存到中转队列
   */
  default void putAsync(E msg) {
    getPutExecutor().execute(() -> put(msg));
  }


}
