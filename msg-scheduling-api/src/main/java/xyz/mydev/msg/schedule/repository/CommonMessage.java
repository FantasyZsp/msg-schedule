package xyz.mydev.msg.schedule.repository;

import java.time.LocalDateTime;

/**
 * @author ZSP
 */
public interface CommonMessage {

  String getId();

  String getTopic();

  int getMqPlatform();

  String getMessage();

  String getSystemContext();

  int getStatus();

  LocalDateTime getCreatedAt();

  LocalDateTime getUpdatedAt();

}
