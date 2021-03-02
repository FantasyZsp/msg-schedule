package xyz.mydev.msg.schedule.bean;

import xyz.mydev.msg.common.MessageBusinessInfo;
import xyz.mydev.msg.common.MessagePlatform;

/**
 * @author ZSP
 */
public interface Message extends StringMessage, MessagePlatform, MessageBusinessInfo {

  String getTraceId();

  String getTraceVersion();

}
