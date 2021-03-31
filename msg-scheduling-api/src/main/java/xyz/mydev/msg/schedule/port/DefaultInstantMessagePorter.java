package xyz.mydev.msg.schedule.port;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.bean.InstantMessage;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ExecutorService;

/**
 * 消息搬运工
 * 处理缓存、去重、可靠中转等逻辑
 *
 * @author ZSP
 */
@Slf4j
@Getter
@Setter
public class DefaultInstantMessagePorter implements Porter<InstantMessage> {

  private final String targetTableName;
  private final Class<?> tableEntityClass;
  private final PortTaskFactory<InstantMessage> portTaskFactory;
  private final TransferTaskFactory<InstantMessage> transferTaskFactory;

  public DefaultInstantMessagePorter(String targetTableName,
                                     Class<?> tableEntityClass,
                                     PortTaskFactory<InstantMessage> portTaskFactory,
                                     TransferTaskFactory<InstantMessage> transferTaskFactory) {
    this.targetTableName = targetTableName;
    this.tableEntityClass = tableEntityClass;
    this.portTaskFactory = portTaskFactory;
    this.transferTaskFactory = transferTaskFactory;
  }


  @Override
  public ExecutorService getTransferExecutor() {
    return null;
  }

  @Override
  public @NotNull TransferTaskFactory<InstantMessage> getTransferTaskFactory() {
    return transferTaskFactory;
  }

  @Override
  public ExecutorService getPortExecutor() {
    return null;
  }

  @Override
  public @NotNull PortTaskFactory<InstantMessage> getPortTaskFactory() {
    return portTaskFactory;
  }

  @Override
  public void init() {

  }

  @Override
  public void shutdown() {

  }

  @Override
  public void transfer(InstantMessage instantMessage) {

  }

  @Override
  public void port(InstantMessage instantMessage) {

  }
}