package xyz.mydev.msg.schedule.port;

import java.util.concurrent.ExecutorService;

/**
 * @author ZSP
 */
public interface Porter<E> {

  ExecutorService getPutExecutor();

  ExecutorService getPortExecutor();

  Runnable newPortTask(E e);

  Runnable newPutTask(E e);

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

  default void put(E e) {

    Runnable putTask = newPutTask(e);

    if (putTask != null) {
      if (getPutExecutor() == null) {
        putTask.run();
      } else {
        getPutExecutor().execute(putTask);
      }
    }
  }

  void start();

  void shutdown();
}
