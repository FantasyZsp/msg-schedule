package xyz.mydev.msg.schedule.port;

import java.util.Queue;

/**
 * 基于委派实现中转队列
 * xyz.mydev.msg.schedule.port.TransferQueue#getTargetQueue() 给出了具体的实现
 *
 * @author ZSP
 */
public interface TransferQueue<E> {

  boolean put(E e);

  E take() throws InterruptedException;

  boolean contains(E e);

  boolean remove(E e);

  /**
   * 目标队列
   */
  Queue<E> getTargetQueue();

  void start();

  void destroy();

}