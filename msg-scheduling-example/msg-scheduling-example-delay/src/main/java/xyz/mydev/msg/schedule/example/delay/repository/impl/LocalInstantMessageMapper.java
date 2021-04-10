package xyz.mydev.msg.schedule.example.delay.repository.impl;

import org.apache.ibatis.annotations.Mapper;
import xyz.mydev.msg.schedule.example.delay.repository.LocalInstantMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * @author ZSP
 */
@Mapper
public interface LocalInstantMessageMapper {
  int insert(LocalInstantMessage entity);

  LocalInstantMessage selectById(String id);

  int updateToSent(String id);

  int updateStatus(String id, int status);

  List<LocalInstantMessage> findWillSend(LocalDateTime startTime, LocalDateTime endTime);

  Optional<LocalDateTime> findCheckpoint();

  Optional<LocalDateTime> findNextCheckpointAfter(LocalDateTime oldCheckPoint);
}