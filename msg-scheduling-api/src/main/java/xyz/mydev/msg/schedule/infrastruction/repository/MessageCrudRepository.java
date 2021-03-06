package xyz.mydev.msg.schedule.infrastruction.repository;

import java.util.List;
import java.util.Optional;

/**
 * @author ZSP
 */
public interface MessageCrudRepository<E, ID, CP> {

  E selectById(ID id);

  int insert(E entity);

  boolean updateStatus(String id, int status);

  boolean updateToSent(ID id);

  List<E> findWillSendBetween(CP startTime, CP endTime);

  Optional<CP> findCheckpoint();

  /**
   * 返回晚于指定时间 oldCheckPoint 的检查点，由于表中可能没有任何数据，所以可能不存在这样的结果。
   *
   * @param oldCheckPoint 给定的时间点，一般是上次记录的检查点
   * @return 返回晚于指定时间的检查点
   */
  Optional<CP> findNextCheckpointAfter(CP oldCheckPoint);


}
