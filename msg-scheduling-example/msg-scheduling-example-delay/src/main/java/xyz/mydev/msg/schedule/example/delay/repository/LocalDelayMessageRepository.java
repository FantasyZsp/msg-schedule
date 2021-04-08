package xyz.mydev.msg.schedule.example.delay.repository;

import xyz.mydev.msg.schedule.example.delay.repository.impl.LocalDelayMessageDao;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author ZSP
 */
public class LocalDelayMessageRepository implements MessageRepository<LocalDelayMessage> {

  private final LocalDelayMessageDao localDelayMessageDao;

  public LocalDelayMessageRepository(LocalDelayMessageDao localDelayMessageDao) {
    this.localDelayMessageDao = localDelayMessageDao;
  }

  @Override
  public String getTableName() {
    return LocalDelayMessage.TARGET_TABLE_NAME;
  }

  @Override
  public LocalDelayMessage selectById(String id) {
    return localDelayMessageDao.selectById(id);
  }

  @Override
  public int insert(LocalDelayMessage entity) {
    return localDelayMessageDao.insert(entity);
  }

  @Override
  public boolean updateStatus(String id, int status) {
    return localDelayMessageDao.updateStatus(id, status, null) > 0;
  }

  @Override
  public boolean updateToSent(String id) {
    return localDelayMessageDao.updateToSent(id, null) > 0;
  }

  @Override
  public List<LocalDelayMessage> findWillSendBetween(LocalDateTime startTime, LocalDateTime endTime) {
    return localDelayMessageDao.findWillSend(startTime, endTime);
  }

  @Override
  public Optional<LocalDateTime> findCheckpoint() {
    return localDelayMessageDao.findCheckpoint();
  }

  @Override
  public Optional<LocalDateTime> findNextCheckpointAfter(LocalDateTime oldCheckPoint) {
    return localDelayMessageDao.findNextCheckpointAfter(oldCheckPoint);
  }
}