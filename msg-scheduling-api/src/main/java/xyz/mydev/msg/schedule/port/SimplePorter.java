package xyz.mydev.msg.schedule.port;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.common.util.CallerRunsPolicy;
import xyz.mydev.msg.common.util.PrefixNameThreadFactory;
import xyz.mydev.msg.schedule.bean.BaseMessage;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 消息搬运工
 * 处理缓存、去重、可靠中转等逻辑
 *
 * @author ZSP
 */
@Slf4j
@Getter
public class SimplePorter<E extends BaseMessage<String>> extends AbstractPorterThread<E> {

  private final Function<E, Runnable> runnableFunction;
  @Setter
  private Executor executor;


  public SimplePorter(String name,
                      TransferQueue<E> transferQueue,
                      Function<E, Runnable> runnableFunction) {
    super(name, transferQueue);
    this.runnableFunction = Objects.requireNonNull(runnableFunction);
  }

  @Override
  public void port(E msg) {
    executor.execute(runnableFunction.apply(msg));
  }

  @Override
  public void init() {
    super.init();
    initExecutor();
  }

  public void initExecutor() {
    log.info("init porter executor");
    this.executor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.HOURS,
      new LinkedBlockingQueue<>(2000),
      new PrefixNameThreadFactory("PorterPool"),
      new CallerRunsPolicy(getTransferQueue().getTargetQueue())
    );
  }
}