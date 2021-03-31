package xyz.mydev.msg.schedule;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 配置表的调度时必要的参数
 *
 * @author ZSP
 */
@Getter
@Setter
@ToString
public class TableScheduleProperties {
  private String tableName;
  private Integer checkpointInterval = 15;
  private Integer loadInterval = 30;

  private Class<?> tableEntityClass;
  private Boolean isDelay;
  private Boolean useDefaultConfig = false;
}
