package xyz.mydev.msg.schedule.port;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.common.util.CallerRunsPolicy;
import xyz.mydev.msg.common.util.PrefixNameThreadFactory;
import xyz.mydev.msg.schedule.bean.DelayMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 消息搬运工
 * 处理缓存、去重、可靠中转等逻辑
 *
 * @author ZSP
 */
@Slf4j
@Getter
@Setter
public class DefaultDelayMessagePorter extends AbstractPorter<DelayMessage> {

  private ExecutorService portExecutor;
  private ExecutorService transferExecutor;

  public DefaultDelayMessagePorter(String targetTableName,
                                   TransferQueue<DelayMessage> transferQueue,
                                   TransferTaskFactory<DelayMessage> transferTaskFactory,
                                   PortTaskFactory<DelayMessage> portTaskFactory) {
    super(targetTableName, transferQueue, transferTaskFactory, portTaskFactory);
  }

  @Override
  public void init() {
    super.init();
    initExecutor();
  }

  public void initExecutor() {
    log.info("init porter executor");
    if (portExecutor == null) {
      this.portExecutor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.HOURS,
        new LinkedBlockingQueue<>(2000),
        new PrefixNameThreadFactory(getName() + "-port"),
        new CallerRunsPolicy(getTransferQueue().getTargetQueue())
      );
    }

    if (transferExecutor == null) {
      this.transferExecutor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.HOURS,
        new LinkedBlockingQueue<>(2000),
        new PrefixNameThreadFactory(getName() + "-trans"),
        new CallerRunsPolicy(getTransferQueue().getTargetQueue())
      );
    }
  }
}