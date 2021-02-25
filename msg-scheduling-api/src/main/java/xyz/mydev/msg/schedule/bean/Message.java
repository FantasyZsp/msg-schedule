package xyz.mydev.msg.schedule.bean;

import xyz.mydev.msg.common.MessageBusinessId;
import xyz.mydev.msg.common.MessagePlatform;

/**
 * @author ZSP
 */
public interface Message extends BaseMessage<String>, MessagePlatform, MessageBusinessId {

  String getTraceId();

  String getTraceVersion();

  String getSystemContext();

}
