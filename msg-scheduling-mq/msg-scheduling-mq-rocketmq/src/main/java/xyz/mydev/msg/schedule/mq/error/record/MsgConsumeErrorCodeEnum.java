package xyz.mydev.msg.schedule.mq.error.record;

import lombok.Getter;

/**
 * @author ZSP
 */
@Getter
public enum MsgConsumeErrorCodeEnum {
  /**
   * 1 未知
   */
  UNKNOWN(1001, "unknown"),
  /**
   * 达到了最大的重试次数
   */
  CONSUME_REACH_MAX_RETRY_TIMES(1100, "reachMaxRetryTimes");

  private final int code;
  private final String description;

  MsgConsumeErrorCodeEnum(int code, String description) {
    this.code = code;
    this.description = description;
  }
}
