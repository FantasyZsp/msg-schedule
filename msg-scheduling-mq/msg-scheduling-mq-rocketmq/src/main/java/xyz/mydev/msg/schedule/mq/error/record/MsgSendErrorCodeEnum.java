package xyz.mydev.msg.schedule.mq.error.record;

import lombok.Getter;

/**
 * @author ZSP
 */
@Getter
public enum MsgSendErrorCodeEnum {
  /**
   * 1 未知。可能是客户端本身，或网络或路由导致的。也许发送失败表中的错误描述会有帮助。
   */
  UNKNOWN(1, "unknown"),

  /**
   * 半消息发送错误
   */
  HALF_MSG(2, "halfMsgError"),

  /**
   * 执行事务本地方法出错（对于消息表，指修改消息发送状态）
   */
  EXECUTE_LOCAL_TX(100, "localTxError"),

  /**
   * 检查事务环节错误
   */
  CHECK_LOCAL_TX(200, "checkLocalTxError"),
  /**
   * 检查事务环节错误，且消息库无消息
   * 一般是半消息发送后，后续流程异常且没有回滚半消息导致
   */
  CHECK_LOCAL_TX_MAX_RETRY(201, "reachMaxRetryTimes"),

  CHECK_LOCAL_TX_MAX_RETRY_NO_MSG(202, "reachMaxRetryTimesAndNoMsgInDb"),

  /**
   * 同步消息发送
   */
  SEND_SYNC(600, "sendSyncError");

  private final int code;
  private final String description;

  MsgSendErrorCodeEnum(int code, String description) {
    this.code = code;
    this.description = description;
  }
}
