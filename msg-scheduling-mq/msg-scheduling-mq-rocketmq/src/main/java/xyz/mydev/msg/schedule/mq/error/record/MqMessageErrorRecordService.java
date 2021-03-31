package xyz.mydev.msg.schedule.mq.error.record;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import xyz.mydev.msg.schedule.IdGenerator;

/**
 * @author ZSP
 */
@Service
public class MqMessageErrorRecordService {
  private final MqMessageErrorRecordDao mqMessageErrorRecordDao;
  private final IdGenerator idGenerator;

  public MqMessageErrorRecordService(MqMessageErrorRecordDao mqMessageErrorRecordDao, IdGenerator idGenerator) {
    this.mqMessageErrorRecordDao = mqMessageErrorRecordDao;
    this.idGenerator = idGenerator;
  }


  public int save(MqMessageErrorRecord entity) {
    if (StringUtils.isBlank(entity.getId())) {
      entity.setId(idGenerator.get());
    }
    return mqMessageErrorRecordDao.insert(entity);
  }

  public int delete(String id) {
    Preconditions.checkArgument(StringUtils.isNotBlank(id));
    return mqMessageErrorRecordDao.deleteByPrimaryKey(id);
  }

}
