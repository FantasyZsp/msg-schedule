package xyz.mydev.msg.schedule.port;

import xyz.mydev.msg.schedule.TableScheduleProperties;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Objects;
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
 * 约束：
 * 如果需要往IOC容器注册bean，那么必须保证{@code getTableScheduleProperties()}返回的是非空
 *
 * @author ZSP
 */
public interface Porter<E> {

  @NotNull
  String getTargetTableName();

  /**
   * 如果需要往IOC容器注册bean，那么必须保证返回的是非空且能够通过{@link TableScheduleProperties#validate}校验
   * 通过yml配置的组件不需要事先此项，但必须在yml中声明必要属性，一般是结合default配置项，通过{@link TableScheduleProperties#validate}校验
   */
  @Nullable
  default TableScheduleProperties getTableScheduleProperties() {
    return new TableScheduleProperties();
  }

  void setTableScheduleProperties(TableScheduleProperties tableScheduleProperties);

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


  default void init() {
    Objects.requireNonNull(getTableScheduleProperties()).validate();
  }

  /**
   * TODO 抽离抽象初始化行为。子类有很多相同的实现
   */
  default void shutdown() {
    if (getTransferExecutor() != null) {
      getTransferExecutor().shutdown();
    }

    if (getPortExecutor() != null) {
      getPortExecutor().shutdown();
    }
  }
}
