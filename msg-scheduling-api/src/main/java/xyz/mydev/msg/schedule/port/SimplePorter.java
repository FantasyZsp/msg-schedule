package xyz.mydev.msg.schedule.port;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.bean.BaseMessage;

import java.util.concurrent.Executor;
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
    this.runnableFunction = runnableFunction;
  }

  @Override
  public void port(E msg) {

  }
}