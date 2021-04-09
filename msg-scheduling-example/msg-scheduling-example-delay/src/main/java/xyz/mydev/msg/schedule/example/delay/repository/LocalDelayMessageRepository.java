package xyz.mydev.msg.schedule.example.delay.repository;

import org.springframework.stereotype.Repository;
import xyz.mydev.msg.schedule.example.delay.repository.impl.LocalDelayMessageMapper;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author ZSP
 */
@Repository
public class LocalDelayMessageRepository implements MessageRepository<LocalDelayMessage> {

  private final LocalDelayMessageMapper localDelayMessageMapper;

  public LocalDelayMessageRepository(LocalDelayMessageMapper localDelayMessageMapper) {
    this.localDelayMessageMapper = localDelayMessageMapper;
  }

  @Override
  public String getTableName() {
    return LocalDelayMessage.TARGET_TABLE_NAME;
  }

  @Override
  public LocalDelayMessage selectById(String id) {
    return localDelayMessageMapper.selectById(id);
  }

  @Override
  public int insert(LocalDelayMessage entity) {
    return localDelayMessageMapper.insert(entity);
  }

  @Override
  public boolean updateStatus(String id, int status) {
    return localDelayMessageMapper.updateStatus(id, status) > 0;
  }

  @Override
  public boolean updateToSent(String id) {
    return localDelayMessageMapper.updateToSent(id) > 0;
  }

  @Override
  public List<LocalDelayMessage> findWillSendBetween(LocalDateTime startTime, LocalDateTime endTime) {
    return localDelayMessageMapper.findWillSend(startTime, endTime);
  }

  @Override
  public Optional<LocalDateTime> findCheckpoint() {
    return localDelayMessageMapper.findCheckpoint();
  }

  @Override
  public Optional<LocalDateTime> findNextCheckpointAfter(LocalDateTime oldCheckPoint) {
    return localDelayMessageMapper.findNextCheckpointAfter(oldCheckPoint);
  }
}