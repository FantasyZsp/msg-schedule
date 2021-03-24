package xyz.mydev.msg.schedule.delay.port;

import lombok.Data;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.port.Porter;
import xyz.mydev.msg.schedule.port.route.PorterRouter;

import java.util.Collection;

/**
 * 依赖外部化配置初始化所有的porter
 *
 * @author ZSP
 */
@Data
public class DelayMessagePorterRouter implements PorterRouter {


  private Collection<Porter<? super StringMessage>> porters;


  @Override
  public Porter<? super StringMessage> get(String key) {
    return null;
  }

  /**
   * todo
   */
  public void initPorters() {

  }

}
