package xyz.mydev.msg.schedule.port;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.common.util.CallerRunsPolicy;
import xyz.mydev.msg.common.util.PrefixNameThreadFactory;
import xyz.mydev.msg.schedule.bean.StringMessage;

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
public class DefaultPorter<E extends StringMessage> extends AbstractPorter<E> {

  private ExecutorService portExecutor;
  private ExecutorService transferExecutor;

  public DefaultPorter(String name,
                       TransferQueue<E> transferQueue) {
    super(name, transferQueue);
  }

  @Override
  public ExecutorService getTransferExecutor() {
    return transferExecutor;
  }

  @Override
  public ExecutorService getPortExecutor() {
    return portExecutor;
  }

  @Override
  public Runnable newPortTask(E e) {
    return null;
  }

  @Override
  public Runnable newTransferTask(E e) {
    return () -> getTransferQueue().put(e);
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