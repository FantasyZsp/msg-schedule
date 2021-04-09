package xyz.mydev.msg.schedule.mq.error.record;

/**
 * @author ZSP
 */
public interface ErrorMessageRepository {
  int insert(ErrorMessage record);
}