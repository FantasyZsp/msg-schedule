package xyz.mydev.msg.schedule.port;

/**
 * @author ZSP
 */
@FunctionalInterface
public interface TransferTaskFactory<E> extends TaskFactory<E> {

  default Runnable newTransferTask(E e) {
    return newTask(e);
  }

}
