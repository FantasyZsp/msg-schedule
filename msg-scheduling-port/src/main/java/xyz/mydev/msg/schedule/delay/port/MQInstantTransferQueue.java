package xyz.mydev.msg.schedule.delay.port;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.bean.InstantMessage;
import xyz.mydev.msg.schedule.port.TransferQueue;

import java.util.Queue;

/**
 * TODO 直接投递到mq
 *
 * @author ZSP
 */
@Slf4j
public class MQInstantTransferQueue<E extends InstantMessage> implements TransferQueue<E> {


  @Override
  public boolean put(E e) {
    return false;
  }

  @Override
  public E take() throws InterruptedException {
    return null;
  }

  @Override
  public boolean contains(E e) {
    return false;
  }

  @Override
  public boolean remove(E e) {
    return false;
  }

  @Override
  public Queue<E> getTargetQueue() {
    return null;
  }

  @Override
  public void start() {

  }

  @Override
  public void destroy() {

  }
}
