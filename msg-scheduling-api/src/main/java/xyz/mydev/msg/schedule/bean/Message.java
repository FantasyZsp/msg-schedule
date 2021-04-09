package xyz.mydev.msg.schedule.bean;

import xyz.mydev.msg.common.MessageBusinessInfo;
import xyz.mydev.msg.common.MessagePlatform;

import javax.annotation.Nullable;

/**
 * @author ZSP
 */
public interface Message extends StringMessage, MessagePlatform, MessageBusinessInfo {

  @Nullable
  String getTraceId();

  void setTraceId(String traceId);

  @Nullable
  String getTraceVersion();

  void setTraceVersion(String traceVersion);

}
