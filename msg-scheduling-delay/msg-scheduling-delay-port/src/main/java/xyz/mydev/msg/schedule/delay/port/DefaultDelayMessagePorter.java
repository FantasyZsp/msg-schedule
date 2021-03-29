package xyz.mydev.msg.schedule.delay.port;

import xyz.mydev.msg.schedule.bean.SerializableMessage;
import xyz.mydev.msg.schedule.port.DefaultPorter;
import xyz.mydev.msg.schedule.port.PortTaskFactory;
import xyz.mydev.msg.schedule.port.Porter;
import xyz.mydev.msg.schedule.port.TransferTaskFactory;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;

/**
 * @author ZSP
 */
public class DefaultDelayMessagePorter<T> implements Porter<T> {


  private DefaultPorter<? extends SerializableMessage<? extends Serializable>> defaultPorter;


  public static <E> Porter<E> build(Class<E> tClass) {
    return new DefaultDelayMessagePorter<>();
  }

  @Override
  public ExecutorService getTransferExecutor() {
    return null;
  }

  @Override
  public @NotNull TransferTaskFactory<T> getTransferTaskFactory() {
    return null;
  }

  @Override
  public ExecutorService getPortExecutor() {
    return null;
  }

  @Override
  public @NotNull PortTaskFactory<T> getPortTaskFactory() {
    return null;
  }

  @Override
  public void init() {

  }

  @Override
  public void shutdown() {

  }
}
