package xyz.mydev.msg.schedule.infrastruction.repository;

import xyz.mydev.msg.schedule.bean.StringMessage;

import java.time.LocalDateTime;

/**
 * @author ZSP
 */
public interface MessageRepository<T extends StringMessage> extends MessageCrudRepository<T, String, LocalDateTime> {

  String getTableName();

}
