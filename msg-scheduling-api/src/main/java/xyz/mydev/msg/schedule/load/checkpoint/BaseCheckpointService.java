package xyz.mydev.msg.schedule.load.checkpoint;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author ZSP
 */
public interface BaseCheckpointService<K, CP> {

  /**
   * 默认检查点，一般用于初始化时或者读取到null时的垫底
   *
   * @return 默认检查点
   */
  CP defaultStartCheckpoint();

  /**
   * 读取当前检查点。当存储或缓存中找不到时，默认返回 {@link BaseCheckpointService#defaultStartCheckpoint}
   *
   * @param targetTableName 所服务的消息表名称
   * @return 返回当前检查点，永远不会为null
   */
  CP readCheckpoint(K targetTableName);


  /**
   * 读取下个检查点。当存储或缓存中找不到时，默认返回 当前的检查点。
   * 当前检查点最起码会是 {@link BaseCheckpointService#defaultStartCheckpoint()}
   *
   * @param targetTableName   所服务的消息表名称
   * @param currentCheckpoint 当前检查点
   * @return 返回下个检查点，永远不会为null
   */
  CP readNextCheckpoint(K targetTableName, CP currentCheckpoint);

  /**
   * 读取下个检查点。当存储或缓存中找不到时，默认返回 当前的检查点。
   * 对{@link BaseCheckpointService#readNextCheckpoint(K, CP)}的封装，内部获取 currentCheckpoint
   * 当前检查点最起码会是 {@link BaseCheckpointService#defaultStartCheckpoint()}
   *
   * @param targetTableName 所服务的消息表名称
   * @return 返回下个检查点，永远不会为null
   */
  CP readNextCheckpoint(K targetTableName);

  /**
   * 写入检查点。
   * 由于消息可能存在手动补发，因此实现方根据需要确定是否允许出现检查点前移。
   *
   * @param targetTableName 目标表
   * @param checkpoint      将要写入的检查点，一般是当前最新的检查点
   */
  void writeCheckPoint(K targetTableName, CP checkpoint);

  /**
   * 获取读写锁
   * 锁具体的key可以被加工，如增加前缀
   *
   * @param targetTableName 目标表
   * @return 用于控制写并发
   */
  ReadWriteLock getReadWriteLock(K targetTableName);

  /**
   * 获取一把调度锁
   *
   * @param targetTableName 目标表
   * @return 调度锁，用于控制调度与读写并发
   */
  Lock getScheduleLock(K targetTableName);


}
