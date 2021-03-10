package xyz.mydev.msg.schedule.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhaosp
 */
@Getter
@Setter
@ToString
public class SchedulerProperties {
  ExecutorProperties scheduleExecutor;
  ExecutorProperties portExecutor;
  ExecutorProperties checkpointExecutor;
  TableScheduleProperties defaultScheduleInterval;

  TableRouteProperties route;


  public TableScheduleProperties get(String targetTableName) {
    return route.getTables().getOrDefault(targetTableName, defaultScheduleInterval);
  }


}
