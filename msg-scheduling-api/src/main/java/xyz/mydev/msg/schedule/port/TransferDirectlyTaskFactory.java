package xyz.mydev.msg.schedule.port;

import javax.validation.constraints.NotNull;

/**
 * @author ZSP
 */
public class TransferDirectlyTaskFactory<E> implements TransferTaskFactory<E> {

  TransferQueue<E> transferQueue;

  public TransferDirectlyTaskFactory(TransferQueue<E> transferQueue) {
    this.transferQueue = transferQueue;
  }

  @Override
  public @NotNull Runnable newTask(E e) {
    return () -> transferQueue.put(e);
  }
}
