package xyz.mydev.msg.schedule.infrastruction.repository;

import xyz.mydev.msg.schedule.bean.Message;

import java.time.LocalDateTime;

/**
 * @author ZSP
 */
public interface MessageRepository extends MessageCrudRepository<Message, String, LocalDateTime> {
}
