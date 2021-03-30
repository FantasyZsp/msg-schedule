package xyz.mydev.msg.schedule.delay.autoconfig.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhaosp
 */
@Getter
@Setter
@ToString
public class ExecutorProperties {
  private int minThread;
  private int maxThread;
  private boolean dependOnTableSize = true;

}
