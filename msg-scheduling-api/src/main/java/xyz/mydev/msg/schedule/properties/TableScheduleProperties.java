package xyz.mydev.msg.schedule.properties;

import lombok.Data;

/**
 * 配置表的调度时必要的参数
 *
 * @author ZSP
 */
@Data
public class TableScheduleProperties {
  private int checkpointInterval;
  private int loadInterval;
}
