package xyz.mydev.msg.schedule.delay.port;

import lombok.Setter;
import xyz.mydev.msg.schedule.bean.SerializableMessage;
import xyz.mydev.msg.schedule.port.DefaultPorter;
import xyz.mydev.msg.schedule.port.Porter;
import xyz.mydev.msg.schedule.port.route.PorterRouter;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

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
  private Map<String, Porter<SerializableMessage<? extends Serializable>>> porters;


  @Override
  public Porter<SerializableMessage<? extends Serializable>> get(String key) {
    return porters.get(key);
  }

  @Override
  public void put(String key, Porter<SerializableMessage<? extends Serializable>> val) {
    Objects.requireNonNull(key);
    porters.put(key, val);

  }

  public static void main(String[] args) {
    DelayMessagePorterRouter router = new DelayMessagePorterRouter();
    router.put("key", new DefaultPorter<>(null, null, null, null));
//    router.put("key", new DefaultPorter<DelayMessage>(null, null, null, null));
  }


}
