package xyz.mydev.msg.common;

import javax.annotation.Nullable;

/**
 * 平台消息
 * 关注依托的平台坐标
 *
 * @author ZSP
 */
public interface MessagePlatform extends MessageTag {

  Integer getPlatform();

  void setPlatform(Integer platform);

  @Nullable
  String getPlatformMsgId();

  void setPlatformMsgId(String platformMsgId);

}
