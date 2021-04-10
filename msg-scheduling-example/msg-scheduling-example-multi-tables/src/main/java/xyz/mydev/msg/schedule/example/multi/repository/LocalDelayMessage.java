package xyz.mydev.msg.schedule.example.multi.repository;

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

  /**
   * 和yml中的配置保持一致。同表调度的地方，关于targetTableName都需要统一
   */
  public static final String TARGET_TABLE_NAME = "localDelayMessage";

  private String id;
  private String topic;
  private String tag;
  private Integer isTx = 1;
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

  @Nullable
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

  public static LocalDelayMessage of(String id, String topic, String tag, Integer isTx, String businessId, String payload, LocalDateTime time) {
    LocalDelayMessage localDelayMessage = new LocalDelayMessage();
    localDelayMessage.setId(id); // set by IdGenerator
    localDelayMessage.setTopic(topic);
    localDelayMessage.setTag(tag);
    localDelayMessage.setIsTx(isTx);
    localDelayMessage.setBusinessId(businessId);
    localDelayMessage.setPayload(payload);
    localDelayMessage.setTime(time);

    LocalDateTime now = LocalDateTime.now();
    localDelayMessage.setStatus(0); // default
    localDelayMessage.setCreatedAt(now); // default
    localDelayMessage.setUpdatedAt(now); // default
    return localDelayMessage;
  }
}
