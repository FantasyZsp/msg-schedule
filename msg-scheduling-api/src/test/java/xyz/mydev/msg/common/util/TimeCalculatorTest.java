package xyz.mydev.msg.common.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

/**
 * @author ZSP
 */
class TimeCalculatorTest {

  @Test
  void formatTimeIntervals() {

    System.out.println(TimeCalculator.formatTimeIntervals(LocalDateTime.now(), 30));
    System.out.println(TimeCalculator.formatTimeIntervals(LocalDateTime.now(), 15));
    System.out.println(TimeCalculator.formatTimeIntervals(LocalDateTime.now(), 10));
    System.out.println(TimeCalculator.formatTimeIntervals(LocalDateTime.now(), 5));

  }

  @Test
  void formatTimeFiveIntervals() {
    System.out.println(TimeCalculator.formatTimeFiveIntervals(LocalDateTime.now()));
  }

  @Test
  void formatTimeTenIntervals() {
    System.out.println(TimeCalculator.formatTimeTenIntervals(LocalDateTime.now()));

  }

  @Test
  void formatTimeFifteenIntervals() {

    System.out.println(TimeCalculator.formatTimeFifteenIntervals(LocalDateTime.now()));
  }

  @Test
  void formatTimeThirtyIntervals() {

    System.out.println(TimeCalculator.formatTimeThirtyIntervals(LocalDateTime.now()));
    System.out.println(TimeCalculator.formatTimeThirtyIntervals(LocalDateTime.now().minusMinutes(20)));
  }

  /**
   * 计算当天格式化后时间 序号
   */
  @Test
  void calculateFormatTimeNum() {

    System.out.println(TimeCalculator.calculateFormatTimeNum(TimeCalculator
      .formatTimeThirtyIntervals(LocalDateTime.now()), 30));
  }

  @Test
  void shouldPutDirect() {
    System.out.println(TimeCalculator.shouldPutDirect(LocalDateTime.now().plusMinutes(15), 15));
    System.out.println(TimeCalculator.shouldPutDirect(LocalDateTime.now().minusMinutes(2), 15));
  }
}