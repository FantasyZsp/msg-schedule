package xyz.mydev.msg.schedule.example.multi.repository;

import org.springframework.stereotype.Repository;
import xyz.mydev.msg.schedule.example.multi.repository.impl.LocalInstantMessageMapper;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * TODO 提供自动实现机制，类似mybatis-plus baseMapper
 *
 * @author ZSP
 */
@Repository
public class LocalInstantMessageRepository implements MessageRepository<LocalInstantMessage> {

  private final LocalInstantMessageMapper localInstantMessageMapper;

  public LocalInstantMessageRepository(LocalInstantMessageMapper localInstantMessageMapper) {
    this.localInstantMessageMapper = localInstantMessageMapper;
  }

  /**
   * TODO 内置化这个实现，防止人为配置总出错
   */
  @Override
  public String getTableName() {
    return LocalInstantMessage.TARGET_TABLE_NAME;
  }

  @Override
  public LocalInstantMessage selectById(String id) {
    return localInstantMessageMapper.selectById(id);
  }

  @Override
  public int insert(LocalInstantMessage entity) {
    return localInstantMessageMapper.insert(entity);
  }

  @Override
  public boolean updateStatus(String id, int status) {
    return localInstantMessageMapper.updateStatus(id, status) > 0;
  }

  @Override
  public boolean updateToSent(String id) {
    return localInstantMessageMapper.updateToSent(id) > 0;
  }

  @Override
  public List<LocalInstantMessage> findWillSendBetween(LocalDateTime startTime, LocalDateTime endTime) {
    return localInstantMessageMapper.findWillSend(startTime, endTime);
  }

  @Override
  public Optional<LocalDateTime> findCheckpoint() {
    return localInstantMessageMapper.findCheckpoint();
  }

  @Override
  public Optional<LocalDateTime> findNextCheckpointAfter(LocalDateTime oldCheckPoint) {
    return localInstantMessageMapper.findNextCheckpointAfter(oldCheckPoint);
  }
}