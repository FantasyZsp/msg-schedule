package xyz.mydev.msg.schedule.example.delay.repository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import xyz.mydev.msg.schedule.bean.DelayMessage;

import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * @author ZSP
 */
@Getter
@Setter
@ToString
public class LocalDelayMessage implements DelayMessage {

  public static final String TARGET_TABLE_NAME = LocalDelayMessage.class.getSimpleName();

  private String id;
  private String topic;
  private String tag;
  private Integer isTx;
  /**
   * {@link xyz.mydev.msg.common.Constants.MqPlatform}
   */
  private Integer platform;
  /**
   * 中间件提供的消息标识，如rocketmq中的msgId
   */
  @Nullable
  private String platformMsgId;
  @Nullable
  private String traceId;
  @Nullable
  private String traceVersion;

  private String businessId;

  private String payload;
  /**
   * 生效时间
   */
  private LocalDateTime time;

  /**
   * {@link xyz.mydev.msg.common.Constants.MessageStatus}
   */
  private Integer status;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;


  @Override
  public String getTargetTableName() {
    return TARGET_TABLE_NAME;
  }

  @Override
  public Boolean isTx() {
    return isTx != 0;
  }
}
