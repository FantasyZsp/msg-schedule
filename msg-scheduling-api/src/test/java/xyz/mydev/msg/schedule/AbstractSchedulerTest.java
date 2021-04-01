package xyz.mydev.msg.schedule;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import xyz.mydev.msg.common.util.TimeIntervalCorrector;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ZSP
 */
@Slf4j
class AbstractSchedulerTest {

  private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2, new ThreadFactory() {

    private final AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public Thread newThread(Runnable r) {
      return new Thread(r, "T-" + atomicInteger.incrementAndGet());
    }
  });

  private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();
  private static final AtomicInteger ATOMIC_INTEGER2 = new AtomicInteger();
  private static final Runnable runnableAtFixedRate = () -> {
//    log.info("invoke RateRateRate at {}", LocalDateTime.now().getSecond());
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    log.info("RateRateRate end at  {}, times {}", LocalDateTime.now().getSecond(), ATOMIC_INTEGER.incrementAndGet());
  };

  private static final Runnable runnableWithFixedDelay = () -> {
//    log.info("invoke DelayDelayDelay at {}", LocalDateTime.now().getSecond());
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    log.info("DelayDelayDelay end at  {}, times {}", LocalDateTime.now().getSecond(), ATOMIC_INTEGER2.incrementAndGet());
  };

  @Test
  void start() throws InterruptedException {
    scheduledExecutorService.scheduleAtFixedRate(runnableAtFixedRate, 1, 4, TimeUnit.SECONDS);
    scheduledExecutorService.scheduleWithFixedDelay(runnableWithFixedDelay, 1, 4, TimeUnit.SECONDS);

    // 50s to observe
    Thread.sleep(50_000);
  }

  @Test
  void calculateInitialDelay() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime intervalStart = TimeIntervalCorrector.formatTimeInterval(now, 5);

    System.out.println(now);
    System.out.println(intervalStart);
    System.out.println(now.until(intervalStart.plusMinutes(5), ChronoUnit.MILLIS));

  }
}