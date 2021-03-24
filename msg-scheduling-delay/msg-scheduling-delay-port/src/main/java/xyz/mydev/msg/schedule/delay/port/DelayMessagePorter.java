package xyz.mydev.msg.schedule.delay.port;

import xyz.mydev.msg.schedule.delay.infrastruction.repository.bean.DelayMessage;
import xyz.mydev.msg.schedule.port.DefaultPorter;
import xyz.mydev.msg.schedule.port.PortTaskFactory;
import xyz.mydev.msg.schedule.port.TransferDirectlyTaskFactory;
import xyz.mydev.msg.schedule.port.TransferQueue;
import xyz.mydev.msg.schedule.port.TransferTaskFactory;

/**
 * @author ZSP
 */
public class DelayMessagePorter<E extends DelayMessage> extends DefaultPorter<E> {

  public DelayMessagePorter(String name,
                            TransferQueue<E> transferQueue,
                            PortTaskFactory<E> portTaskFactory) {

    super(name,
      transferQueue,
      new TransferDirectlyTaskFactory<>(transferQueue),
      portTaskFactory);
  }

  public DelayMessagePorter(String name,
                            TransferQueue<E> transferQueue,
                            TransferTaskFactory<E> transferTaskFactory,
                            PortTaskFactory<E> portTaskFactory) {
    super(name, transferQueue, transferTaskFactory, portTaskFactory);
  }
}
