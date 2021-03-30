package xyz.mydev.msg.schedule.delay.port;

import xyz.mydev.msg.schedule.bean.DelayMessage;
import xyz.mydev.msg.schedule.port.DefaultPorter;
import xyz.mydev.msg.schedule.port.PortTaskFactory;
import xyz.mydev.msg.schedule.port.Porter;
import xyz.mydev.msg.schedule.port.TransferQueue;
import xyz.mydev.msg.schedule.port.TransferTaskFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author ZSP
 */
public class DefaultDelayMessagePorter<T extends DelayMessage> extends DefaultPorter<T> {


  public DefaultDelayMessagePorter(String targetTableName,
                                   Class<T> tableEntityClass,
                                   TransferQueue<T> transferQueue,
                                   TransferTaskFactory<T> transferTaskFactory,
                                   PortTaskFactory<T> portTaskFactory) {
    super(targetTableName, tableEntityClass, transferQueue, transferTaskFactory, portTaskFactory);
  }

  public static <E extends DelayMessage> Porter<E> buildDefaultDelayMessagePorter(String targetTableName,
                                                                                  Class<E> tableEntityClass,
                                                                                  TransferQueue<E> transferQueue,
                                                                                  TransferTaskFactory<E> transferTaskFactory,
                                                                                  PortTaskFactory<E> portTaskFactory) {
    return new DefaultDelayMessagePorter<>(targetTableName, tableEntityClass, transferQueue, transferTaskFactory, portTaskFactory);
  }

  @Override
  public ExecutorService getTransferExecutor() {
    return null;
  }

  @Override
  public ExecutorService getPortExecutor() {
    return null;
  }
}
