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

  public static final String TARGET_TABLE_NAME = "delay_message";
  public static final Boolean IS_DELAY = true;
  public static final Boolean IS_TX = true;

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
    return TARGET_TABLE_NAME;
  }

  @Override
  public Boolean isDelay() {
    return IS_DELAY;
  }

  @Override
  public Boolean isTx() {
    return IS_TX;
  }
}
