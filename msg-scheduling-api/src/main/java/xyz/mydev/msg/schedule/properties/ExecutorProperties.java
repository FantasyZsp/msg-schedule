package xyz.mydev.msg.schedule.properties;

import lombok.Data;

/**
 * @author zhaosp
 */
@Data
public class ExecutorProperties {
  private int minThread;
  private int maxThread;
}
