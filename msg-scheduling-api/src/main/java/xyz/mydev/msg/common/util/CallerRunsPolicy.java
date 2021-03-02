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

  private static final AtomicInteger BUSY = new AtomicInteger(0);
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
        log.warn("任务堆积过多!当前任务总量: [{}], 累计交付给调用者执行量: [{}], 队列大小: [{}]", e.getTaskCount() + queue.size(), howBusy, e.getQueue().size());
      }
      r.run();
    }
  }
}