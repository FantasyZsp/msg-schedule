package xyz.mydev.msg.schedule.port;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.common.util.CallerRunsPolicy;
import xyz.mydev.msg.common.util.PrefixNameThreadFactory;
import xyz.mydev.msg.schedule.TableScheduleProperties;
import xyz.mydev.msg.schedule.bean.InstantMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
  private final PortTaskFactory<InstantMessage> portTaskFactory;
  private final TransferTaskFactory<InstantMessage> transferTaskFactory;
  private final ExecutorService transferExecutor;
  private final ExecutorService portExecutor;
  private TableScheduleProperties tableScheduleProperties;


  public DefaultInstantMessagePorter(String targetTableName,
                                     TransferTaskFactory<InstantMessage> transferTaskFactory) {
    this.targetTableName = targetTableName;
    this.transferTaskFactory = transferTaskFactory;
    this.portTaskFactory = new TransferTaskAdapter<>(transferTaskFactory);
    LinkedBlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<>(2000);
    this.transferExecutor = new ThreadPoolExecutor(1, 2, 1, TimeUnit.HOURS,
      linkedBlockingQueue,
      new PrefixNameThreadFactory(targetTableName + "-port"),
      new CallerRunsPolicy(linkedBlockingQueue)
    );

    this.portExecutor = transferExecutor;
  }

  private AtomicBoolean shutdown = new AtomicBoolean(false);

  @Override
  public void shutdown() {
    if (shutdown.compareAndSet(false, true)) {
      shutDownExecutors();
    }
  }

  protected void shutDownExecutors() {
    // input first
    if (getTransferExecutor() != null) {
      getTransferExecutor().shutdownNow();
    }

    if (getPortExecutor() != null) {
      getPortExecutor().shutdownNow();
    }
  }

}