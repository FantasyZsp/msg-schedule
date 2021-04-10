package xyz.mydev.msg.schedule.example.delay.repository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import xyz.mydev.msg.schedule.bean.InstantMessage;

import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * @author ZSP
 */
@Getter
@Setter
@ToString
public class LocalInstantMessage implements InstantMessage {

  public static final String TARGET_TABLE_NAME = "localInstantMessage";

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

  public static LocalInstantMessage of(String id, String topic, String tag, Integer isTx, String businessId, String payload) {
    LocalInstantMessage localInstantMessage = new LocalInstantMessage();
    localInstantMessage.setId(id); // set by IdGenerator
    localInstantMessage.setTopic(topic);
    localInstantMessage.setTag(tag);
    localInstantMessage.setIsTx(isTx);
    localInstantMessage.setBusinessId(businessId);
    localInstantMessage.setPayload(payload);

    LocalDateTime now = LocalDateTime.now();
    localInstantMessage.setStatus(0); // default
    localInstantMessage.setCreatedAt(now); // default
    localInstantMessage.setUpdatedAt(now); // default
    return localInstantMessage;
  }
}
