package xyz.mydev.msg.schedule.mq.error.record;

/**
 * @author ZSP
 */
public interface MqMessageErrorRecordDao {
  int deleteByPrimaryKey(String id);

  int insert(MqMessageErrorRecord record);


}