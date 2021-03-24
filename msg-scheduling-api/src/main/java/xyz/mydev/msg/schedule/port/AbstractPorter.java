package xyz.mydev.msg.schedule.port;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息搬运工
 * 负责消息的投递
 * JVM -> TransferQueue(Cache) -> MQ
 * TransferQueue 承载了将消息从加载到缓存中的任务，可能包含有去重机制，尽可能防止重复投递
 *
 * @author ZSP
 */
@Slf4j
@Getter
public abstract class Porter<E> extends Thread implements Porter<E> {

  private final TransferQueue<E> transferQueue;

  public AbstractPorter(String name,
                        TransferQueue<E> transferQueue) {
    super(name);
    this.transferQueue = transferQueue;
  }

  @Setter
  private PortExceptionHandle portExceptionHandle;

  /**
   * 这里并没有在模板流程里解决掉消费的可靠性，如果需要，子类请覆盖此方法。
   */
  @Override
  public void run() {

    beforeWorking();

    while (!Thread.currentThread().isInterrupted()) {
      try {
        E msg = transferQueue.take();
        port(msg);
      } catch (InterruptedException e) {
        log.error("InterruptedException occur, shutdown the Porter thread: {}", Thread.currentThread().getName(), e);
        Thread.currentThread().interrupt();
        break;
      } catch (Exception e) {
        if (portExceptionHandle != null) {
          portExceptionHandle.handleException(e);
        } else {
          log.error("exception occur, bug ignore it: ", e);
        }
      }
    }
  }

  protected void beforeWorking() {
    log.info("Porter [{}] start working...", getName());
    transferQueue.start();
  }

  @Override
  public void shutdown() {
    interrupt();
    shutDownExecutors();
    transferQueue.destroy();
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


  @Override
  public void init() {
    Runtime.getRuntime().addShutdownHook((new Thread(AbstractPorter.this::shutdown)));
  }

  @Slf4j
  public static class PortExceptionHandle {
    public void handleException(Throwable ignored) {
    }
  }
}