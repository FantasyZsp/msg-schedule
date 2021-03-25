package xyz.mydev.msg.schedule.delay.port;

import lombok.Setter;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.port.Porter;
import xyz.mydev.msg.schedule.port.route.PorterRouter;

import java.util.Map;

/**
 * 依赖外部化配置初始化所有的porter
 *
 * @author ZSP
 */

public class DelayMessagePorterRouter implements PorterRouter {


  /**
   * targetTableName -> porter
   */
  @Setter
  private Map<String, Porter<? super StringMessage>> porters;


  @Override
  public Porter<? super StringMessage> get(String key) {
    return porters.get(key);
  }


}
