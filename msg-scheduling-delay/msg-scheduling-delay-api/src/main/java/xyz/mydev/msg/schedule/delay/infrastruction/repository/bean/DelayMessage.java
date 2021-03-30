package xyz.mydev.msg.schedule.delay.infrastruction.repository.bean;

import lombok.Data;
import xyz.mydev.msg.common.DelayMessageTag;
import xyz.mydev.msg.schedule.bean.Message;

import java.time.LocalDateTime;

/**
 * @author ZSP
 */
@Data
public class DelayMessage implements Message, DelayMessageTag {

  public String targetTableName;
  public Boolean isDelay = true;
  public Boolean isTx = true;

  private String id;
  private String platformMsgId;
  private int platform;
  private String topic;

  private Integer status;
  private String businessId;
  private String payload;
  private LocalDateTime time;

  private String traceId;
  private String traceVersion;


  @Override
  public String getTargetTableName() {
    return targetTableName;
  }

  @Override
  public Boolean isDelay() {
    return isDelay;
  }

  @Override
  public Boolean isTx() {
    return isTx;
  }
}
