package xyz.mydev.msg.schedule.infrastruction.repository;

import xyz.mydev.msg.schedule.bean.Message;

import java.time.LocalDateTime;

/**
 * @author ZSP
 */
public interface MessageRepository<T extends Message> extends MessageCrudRepository<T, String, LocalDateTime> {

  String getTableName();

}
