package xyz.mydev.msg.schedule.example.delay.repository.impl;

import xyz.mydev.msg.schedule.example.delay.repository.LocalDelayMessage;
import xyz.mydev.msg.schedule.example.delay.repository.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * @author ZSP
 */
public interface LocalDelayMessageDao {

  LocalDelayMessage selectById(@Param("id") String id);

  int insert(LocalDelayMessage entity);

  int updateToSent(@Param("id") String id, @Param("mark") String mark);

  List<LocalDelayMessage> findWillSend(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

  int updateStatus(@Param("id") String id, @Param("status") int status, @Param("mark") String mark);

  Optional<LocalDateTime> findCheckpoint();

  Optional<LocalDateTime> findNextCheckpointAfter(@Param("oldCheckPoint") LocalDateTime oldCheckPoint);
}