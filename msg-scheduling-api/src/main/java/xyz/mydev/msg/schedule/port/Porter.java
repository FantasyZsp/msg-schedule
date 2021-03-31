package xyz.mydev.msg.schedule.port;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ExecutorService;

/**
 * transfer and port elements
 * <p>
 * transfer：
 * 对于延时消息，一般需要transfer中转到TransferQueue处理倒计时逻辑；
 * 对于即时消息，可以直接调用transfer投递到mq
 * <p>
 * port:
 * 直接投递到mq。
 * 对于即时消息来说，transfer和port的任务可能是完全一致的。
 * <p>
 *
 * @author ZSP
 */
public interface Porter<E> {

  String getTargetTableName();

  Class<? extends E> getTableEntityClass();

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
