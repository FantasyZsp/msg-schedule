package xyz.mydev.msg.schedule.repository;

/**
 * @author ZSP
 */
public interface IDelayTagMessage extends IDelayMessage, IMessageTag {
  @Override
  default String getTag() {
    return "*";
  }

  /**
   * 业务id
   *
   * @return 业务id
   */
  String getBusinessId();
}
