package xyz.mydev.msg.common;

/**
 * 平台消息
 * 关注依托的平台坐标
 *
 * @author ZSP
 */
public interface MessagePlatform extends MessageTag {

  Integer getPlatform();

  String getPlatformMsgId();

}
