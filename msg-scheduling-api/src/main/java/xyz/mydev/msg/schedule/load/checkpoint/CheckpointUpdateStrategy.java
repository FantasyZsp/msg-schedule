package xyz.mydev.msg.schedule.load.checkpoint;

/**
 * @author ZSP
 */
public interface CheckpointUpdateStrategy {
  void updateCheckpoint(String targetTableName);
}
