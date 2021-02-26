package xyz.mydev.msg.schedule.infrastruction.repository;

import xyz.mydev.msg.schedule.bean.BaseMessage;

import java.time.LocalDateTime;

/**
 * @author ZSP
 */
public interface MessageRepository<T extends BaseMessage<String>> extends MessageCrudRepository<T, String, LocalDateTime> {

  String getTableName();

}
