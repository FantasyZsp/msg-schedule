package xyz.mydev.msg.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ZSP
 */
@Slf4j
public class CallerRunsPolicy implements RejectedExecutionHandler {

  private final AtomicInteger BUSY = new AtomicInteger(0);
  private final Queue<?> queue;

  public CallerRunsPolicy(Queue<?> queue) {
    this.queue = queue;
  }

  @Override
  public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    int howBusy = BUSY.incrementAndGet();
    if (!e.isShutdown()) {
      int standard = 10;
      if (howBusy >= standard && howBusy % standard == 0) {
        log.warn("Too many task at queue! Current task count: [{}], caller invoke size: [{}], queue size: [{}]", e.getTaskCount() + queue.size(), howBusy, e.getQueue().size());
      }
      r.run();
    }
  }
}