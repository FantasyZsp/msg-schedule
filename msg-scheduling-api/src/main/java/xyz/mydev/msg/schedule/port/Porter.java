package xyz.mydev.msg.schedule.port;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ExecutorService;

/**
 * transfer and port elements
 * <p>
 *
 * @author ZSP
 */
public interface Porter<E> {

  ExecutorService getTransferExecutor();

  @NotNull
  TransferTaskFactory<E> getTransferTaskFactory();

  default void transfer(E e) {
    Runnable transferTask = getTransferTaskFactory().newTransferTask(e);

    if (getTransferExecutor() == null) {
      transferTask.run();
    } else {
      getTransferExecutor().execute(transferTask);
    }
  }

  ExecutorService getPortExecutor();

  @NotNull
  PortTaskFactory<E> getPortTaskFactory();

  default void port(E e) {
    Runnable portTask = getPortTaskFactory().newPortTask(e);
    if (getPortExecutor() == null) {
      portTask.run();
    } else {
      getPortExecutor().execute(portTask);
    }
  }

  void init();

  void shutdown();
}
