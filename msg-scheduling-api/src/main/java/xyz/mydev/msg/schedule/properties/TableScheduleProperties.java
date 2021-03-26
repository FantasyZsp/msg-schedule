package xyz.mydev.msg.schedule.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * 配置表的调度时必要的参数
 *
 * @author ZSP
 */
@Getter
@Setter
public class TableScheduleProperties {
  private String tableName;
  private int checkpointInterval = 15;
  private int loadInterval = 30;

  private Class<?> tableEntityClass;
  private boolean isDelay;
  private boolean useDefaultConfig = false;
}
