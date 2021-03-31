package xyz.mydev.msg.schedule.port;

/**
 * @author zhaosp
 */
public class TransferTaskAdapter<E> implements PortTaskFactory<E> {
  private final TransferTaskFactory<E> transferTaskFactory;

  public TransferTaskAdapter(TransferTaskFactory<E> transferTaskFactory) {
    this.transferTaskFactory = transferTaskFactory;
  }

  @Override
  public Runnable newTask(E e) {
    return transferTaskFactory.newTransferTask(e);
  }
}