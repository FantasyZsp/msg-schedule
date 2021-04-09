package xyz.mydev.msg.schedule.mq.error.record;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 错误消息。发送失败或者消费失败
 *
 * @author ZSP
 */
@Data
@Builder
public class ErrorMessage implements Serializable {

  public static final int ERROR_TYPE_CONSUME = -2;
  public static final int ERROR_TYPE_SEND = -1;

  public static final int MATCHED = 1;
  public static final int NOT_MATCHED = 2;

  /**
   * 主键id
   */
  private String id;


  /**
   * 本地消息表id
   */
  private String msgId;
  /**
   * msg_id是否匹配到主消息表 1匹配 2不匹配(生产环节一定一致，但是消费环节，可能消费到不是主表中有的消息)
   */
  private Integer matched;
  private String topic;
  private Integer platform;
  private String platformMsgId;
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