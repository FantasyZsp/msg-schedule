package xyz.mydev.msg.schedule.port;

import java.util.Queue;

/**
 * @author ZSP
 */
public interface TransferQueue<E> {

  boolean put(E e);

  E take() throws InterruptedException;

  boolean contains(E e);

  boolean remove(E e);

  /**
   * 目标就绪队列
   */
  Queue<E> getTargetQueue();

  void start();

  void destroy();

}