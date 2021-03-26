package xyz.mydev.msg.schedule.infrastruction.repository;

import xyz.mydev.msg.schedule.bean.StringMessage;

/**
 * @author ZSP
 */
public interface MessageRepository<T extends StringMessage> extends GenericMessageRepository<String, T> {

}
