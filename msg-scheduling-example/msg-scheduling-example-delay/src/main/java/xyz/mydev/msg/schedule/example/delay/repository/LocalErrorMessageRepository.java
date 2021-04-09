package xyz.mydev.msg.schedule.example.delay.repository;

import org.springframework.stereotype.Repository;
import xyz.mydev.msg.schedule.example.delay.repository.impl.ErrorMessageMapper;
import xyz.mydev.msg.schedule.mq.error.record.ErrorMessage;
import xyz.mydev.msg.schedule.mq.error.record.ErrorMessageRepository;

/**
 * @author ZSP
 */
@Repository
public class LocalErrorMessageRepository implements ErrorMessageRepository {

  private final ErrorMessageMapper errorMessageMapper;

  public LocalErrorMessageRepository(ErrorMessageMapper errorMessageMapper) {
    this.errorMessageMapper = errorMessageMapper;
  }


  @Override
  public int insert(ErrorMessage entity) {
    return errorMessageMapper.insert(entity);
  }

}