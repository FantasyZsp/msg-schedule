package xyz.mydev.msg.schedule.infrastruction.repository;

import java.util.List;
import java.util.Optional;

/**
 * @author ZSP
 */
public interface MessageCrudRepository<E, ID, CP> {

  Optional<E> selectById(ID id);

  int insert(E entity);

  int update(E entity);

  List<E> findLimitSizeWillSendBetween(CP startTime, CP endTime, int limitSize);

  List<E> findWillSendBetween(CP startTime, CP endTime);

  long countWillSendBetween(CP startTime, CP endTime);

  Optional<CP> findCheckpoint();

  Optional<CP> findNextCheckpointAfter(CP oldCheckPoint);

  boolean existById(String id);

}
