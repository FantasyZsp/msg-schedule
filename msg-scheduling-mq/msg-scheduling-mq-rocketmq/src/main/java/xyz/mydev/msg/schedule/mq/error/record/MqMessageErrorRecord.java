package xyz.mydev.msg.schedule.mq.error.record;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * mq_message_error_record
 *
 * @author ZSP
 */
@Data
@Builder
public class MqMessageErrorRecord implements Serializable {

  public static final int ERROR_TYPE_CONSUME = -2;
  public static final int ERROR_TYPE_SEND = -1;

  public static final int MATCHED = 1;
  public static final int NOT_MATCHED = 2;

  /**
   * 主键id
   */
  private String id;


  /**
   * 具体消息表id，如mq_message_delay.id,mq_message_tc.id
   */
  private String msgId;
  /**
   * msg_id是否匹配到主消息表 1匹配 2不匹配(生产环节一定一致，但是消费环节，可能消费到不是主表中有的消息)
   */
  private Integer matched;
  /**
   * 消息发送管道或topic
   */
  private String channel;


  /**
   * mq平台 1 RocketMQ 2 RabbitMQ 3 Kafka 4其他
   */
  private Integer mqPlatform;
  /**
   * 中间件提供的消息标识，如rocketmq中的msgId
   */
  private String mqPlatformMsgId;


  private String businessId;

  /**
   * 错误类型: -1发送失败 -2消费失败
   */
  private Integer errorType;

  /**
   * 已重试次数。
   * 对于发送时，由自实现的计数器记录；对于消费失败，一般由MQ自带的计数器提供，没有时自实现。
   */
  private Integer retryTimes;

  /**
   * 当一次补偿失败后，记录下retryTimes。基于最终失败的状态，如果后续手动继续进行补偿，retryTimesWhenFailed将开始计数。
   * 非手动重试时，默认为0
   */
  private Integer retryTimesWhenFailed;

  /**
   * 描述，可用于记录异常原因等
   */
  private String errorReason;
  /**
   * 异常码
   * {@link MsgSendErrorCodeEnum}
   */
  private Integer errorCode;

  /**
   * 创建时间
   */
  private LocalDateTime createdAt;

  private static final long serialVersionUID = 1L;
}