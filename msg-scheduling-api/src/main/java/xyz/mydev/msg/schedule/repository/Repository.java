package xyz.mydev.msg.schedule.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author ZSP
 */
public interface Repository<T, ID> {

  T selectById(ID id);

  int insert(T entity);


  List<T> findWillSendBetween(LocalDateTime startTime, LocalDateTime endTime, int limitSize);

  long countWillSendBetween(LocalDateTime startTime, LocalDateTime endTime);

  List<T> findWillSend(LocalDateTime startTime, LocalDateTime endTime);

  int updateStatus(ID id, String mark);

  int updateStatus(String id, int status, String mark);

  int updateStatusWhenConsumeError(String id, int status, String mark);

  Optional<LocalDateTime> findCheckpoint();

  Optional<LocalDateTime> findNextCheckpointAfter(LocalDateTime oldCheckPoint);

  boolean existById(String id);


}
