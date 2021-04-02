package xyz.mydev.msg.schedule;

import xyz.mydev.msg.schedule.bean.StringMessage;

/**
 * @author ZSP
 */
public interface MessageStoreService {
  void store(StringMessage messageEntity);
}
