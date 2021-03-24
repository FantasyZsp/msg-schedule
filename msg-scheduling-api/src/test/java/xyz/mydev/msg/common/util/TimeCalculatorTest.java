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
}