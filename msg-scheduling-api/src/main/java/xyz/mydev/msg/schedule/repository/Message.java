package xyz.mydev.msg.schedule.repository;

/**
 * @author ZSP
 */
public interface Message extends CommonMessage {

  String getMqPlatformMsgId();

  String getBusinessId();

  Integer getRetryTimesWhenFailed();

  Integer getTotalRetryTimes();

  String getMark();

}
