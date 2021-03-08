package xyz.mydev.msg.schedule;

/**
 * @author ZSP
 */
public interface TaskTimeType {


  TaskTimeTypeEnum getTaskTimeType();


  enum TaskTimeTypeEnum {

    /**
     * 检查点
     */
    CheckpointTimeTask,
    /**
     * 固定间隔
     */
    IntervalTimeTask;
  }

}
