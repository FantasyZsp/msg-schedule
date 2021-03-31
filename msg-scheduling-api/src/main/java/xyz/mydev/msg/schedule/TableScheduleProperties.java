package xyz.mydev.msg.schedule;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

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

  public TableScheduleProperties validate() {
    Objects.requireNonNull(tableName);
    Objects.requireNonNull(checkpointInterval);
    Objects.requireNonNull(loadInterval);
    Objects.requireNonNull(tableEntityClass);
    Objects.requireNonNull(isDelay);
    Objects.requireNonNull(useDefaultConfig);
    return this;
  }
}
