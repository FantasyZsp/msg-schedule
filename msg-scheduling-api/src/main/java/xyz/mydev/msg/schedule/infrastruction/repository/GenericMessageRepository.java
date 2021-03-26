package xyz.mydev.msg.schedule.infrastruction.repository;

import xyz.mydev.msg.schedule.bean.SerializableMessage;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhaosp
 */
public interface GenericMessageRepository<S extends Serializable, T extends SerializableMessage<S>> extends MessageCrudRepository<T, S, LocalDateTime> {

  String getTableName();

}