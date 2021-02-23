package xyz.mydev.msg.schedule.load;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.locks.Lock;

/**
 * 未发送的消息检查点
 * 基于时间索引
 * <p>
 *
 * @author ZSP
 */
public interface CheckPointService {

  LocalDateTime DEFAULT_CHECK_POINT = LocalDateTime.of(LocalDate.ofYearDay(2000, 1), LocalTime.MIN);


  /**
   * 读取当前检查点。当存储或缓存中找不到时，默认返回 {@link CheckPointService#DEFAULT_CHECK_POINT}
   *
   * @return 返回当前检查点，永远不会为null
   */
  LocalDateTime readCheckPoint();

  /**
   * 读取下个检查点。当存储或缓存中找不到时，默认返回 currentCheckPoint。
   * 一般用于更新检查点时的读取，可能是一个很重的操作。
   *
   * @param currentCheckPoint 要求非空
   * @return 返回下个检查点，永远不会为null
   */
  LocalDateTime readNextCheckpoint(LocalDateTime currentCheckPoint);

  /**
   * 读取下个检查点。当存储或缓存中找不到时，默认返回 当前的检查点。当前检查点最起码会是 {@link this#DEFAULT_CHECK_POINT}
   *
   * @return 返回下个检查点，永远不会为null
   */
  LocalDateTime readNextCheckpoint();

  /**
   * 写入检查点。
   * 由于消息可能存在手动补发，因此实现方根据需要确实是否允许出现检查点前移。
   *
   * @param checkPoint 将要写入的检查点，一般是当前最新的检查点
   */
  void writeCheckPoint(LocalDateTime checkPoint);

  /**
   * 获取一把写锁
   *
   * @return 写锁，用于控制写并发
   */
  Lock getWriteLock();


  /**
   * 获取一把读锁
   *
   * @return 读锁，用于控制读写并发
   */
  Lock getReadLock();

  /**
   * 获取一把调度锁
   *
   * @return 调度锁，用于控制调度并发
   */
  Lock getScheduleLock();


}
