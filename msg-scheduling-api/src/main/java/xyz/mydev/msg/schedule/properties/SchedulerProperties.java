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

  private ExecutorProperties scheduleExecutor;
  private ExecutorProperties portExecutor;
  private ExecutorProperties checkpointExecutor;

  private TableScheduleProperties defaultScheduleInterval;

  private TableRouteProperties route;

}
