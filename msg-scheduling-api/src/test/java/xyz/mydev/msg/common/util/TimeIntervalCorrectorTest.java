package xyz.mydev.msg.common.util;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author ZSP
 */
class TimeIntervalCorrectorTest {

  @Test
  void formatTimeInterval() {
    LocalDateTime snapshotTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 22, 20));

    System.out.println(TimeIntervalCorrector.formatTimeInterval(snapshotTime, 0));
    System.out.println(TimeIntervalCorrector.formatTimeInterval(snapshotTime, 5));
    System.out.println(TimeIntervalCorrector.formatTimeInterval(snapshotTime, 10));
    System.out.println(TimeIntervalCorrector.formatTimeInterval(snapshotTime, 15));
    System.out.println(TimeIntervalCorrector.formatTimeInterval(snapshotTime, 20));
    System.out.println(TimeIntervalCorrector.formatTimeInterval(snapshotTime, 30));
    Assert.assertEquals(TimeIntervalCorrector.formatTimeInterval(snapshotTime, 0), snapshotTime);
    Assert.assertEquals(TimeIntervalCorrector.formatTimeInterval(snapshotTime, 5), snapshotTime.withMinute(20).withSecond(0));
    Assert.assertEquals(TimeIntervalCorrector.formatTimeInterval(snapshotTime, 10), snapshotTime.withMinute(20).withSecond(0));
    Assert.assertEquals(TimeIntervalCorrector.formatTimeInterval(snapshotTime, 15), snapshotTime.withMinute(15).withSecond(0));
    Assert.assertEquals(TimeIntervalCorrector.formatTimeInterval(snapshotTime, 20), snapshotTime.withMinute(20).withSecond(0));
    Assert.assertEquals(TimeIntervalCorrector.formatTimeInterval(snapshotTime, 30), snapshotTime.withMinute(0).withSecond(0));

    System.out.println("=============");
    LocalDateTime snapshotTime2 = LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 37, 20));
    System.out.println(TimeIntervalCorrector.formatTimeInterval(snapshotTime2, 0));
    System.out.println(TimeIntervalCorrector.formatTimeInterval(snapshotTime2, 5));
    System.out.println(TimeIntervalCorrector.formatTimeInterval(snapshotTime2, 10));
    System.out.println(TimeIntervalCorrector.formatTimeInterval(snapshotTime2, 15));
    System.out.println(TimeIntervalCorrector.formatTimeInterval(snapshotTime2, 20));
    System.out.println(TimeIntervalCorrector.formatTimeInterval(snapshotTime2, 30));

    Assert.assertEquals(TimeIntervalCorrector.formatTimeInterval(snapshotTime2, 0), snapshotTime2);
    Assert.assertEquals(TimeIntervalCorrector.formatTimeInterval(snapshotTime2, 5), snapshotTime2.withMinute(35).withSecond(0));
    Assert.assertEquals(TimeIntervalCorrector.formatTimeInterval(snapshotTime2, 10), snapshotTime2.withMinute(30).withSecond(0));
    Assert.assertEquals(TimeIntervalCorrector.formatTimeInterval(snapshotTime2, 15), snapshotTime2.withMinute(30).withSecond(0));
    Assert.assertEquals(TimeIntervalCorrector.formatTimeInterval(snapshotTime2, 20), snapshotTime2.withMinute(20).withSecond(0));
    Assert.assertEquals(TimeIntervalCorrector.formatTimeInterval(snapshotTime2, 30), snapshotTime2.withMinute(30).withSecond(0));

  }

  @Test
  void intervalSequenceNo() {

    LocalDateTime snapshotTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 57, 20));
    Assert.assertEquals(TimeIntervalCorrector.intervalSequenceNo(snapshotTime, 5), 71);
    Assert.assertEquals(TimeIntervalCorrector.intervalSequenceNo(snapshotTime, 10), 35);
    Assert.assertEquals(TimeIntervalCorrector.intervalSequenceNo(snapshotTime, 15), 23);
    Assert.assertEquals(TimeIntervalCorrector.intervalSequenceNo(snapshotTime, 20), 17);
    Assert.assertEquals(TimeIntervalCorrector.intervalSequenceNo(snapshotTime, 30), 11);


  }

  @Test
  void belongInterval() {

    LocalDateTime taskExecuteTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 57, 20));
    LocalDateTime baseTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 43, 20));
    Assert.assertFalse(TimeIntervalCorrector.belongInterval(taskExecuteTime, baseTime, 5));
    Assert.assertFalse(TimeIntervalCorrector.belongInterval(taskExecuteTime, baseTime, 10));
    Assert.assertFalse(TimeIntervalCorrector.belongInterval(taskExecuteTime, baseTime, 15));
    Assert.assertTrue(TimeIntervalCorrector.belongInterval(taskExecuteTime, baseTime, 20));
    Assert.assertTrue(TimeIntervalCorrector.belongInterval(taskExecuteTime, baseTime, 30));


  }
}