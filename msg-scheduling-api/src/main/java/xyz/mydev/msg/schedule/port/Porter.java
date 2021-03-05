package xyz.mydev.msg.schedule.port;

import java.util.concurrent.ExecutorService;

/**
 * transfer and port elements
 *
 * @author ZSP
 */
public interface Porter<E> {

  ExecutorService getTransferExecutor();

  ExecutorService getPortExecutor();

  Runnable newPortTask(E e);

  Runnable newTransferTask(E e);

  default void port(E e) {

    Runnable portTask = newPortTask(e);

    if (portTask != null) {
      if (getPortExecutor() == null) {
        portTask.run();
      } else {
        getPortExecutor().execute(portTask);
      }
    }
  }

  default void transfer(E e) {

    Runnable transferTask = newTransferTask(e);

    if (transferTask != null) {
      if (getTransferExecutor() == null) {
        transferTask.run();
      } else {
        getTransferExecutor().execute(transferTask);
      }
    }
  }

  void start();

  void shutdown();
}
