package xyz.mydev.msg.schedule.autoconfig.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO 开放自定义配置
 *
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
