package xyz.mydev.msg.schedule.port.route;

import xyz.mydev.msg.common.TableKeyPair;
import xyz.mydev.msg.schedule.port.Porter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 依赖外部化配置初始化所有的porter
 *
 * @author ZSP
 */

public class DelayMessagePorterRouter implements PorterRouter {

  /**
   * tableKeyPair -> porter
   */
  private final Map<TableKeyPair<?>, Porter<?>> porters = new ConcurrentHashMap<>();

  @Override
  @SuppressWarnings("unchecked")
  public <T> Porter<T> get(TableKeyPair<T> key) {
    return (Porter<T>) porters.get(key);
  }

  @Override
  public <T> void put(TableKeyPair<T> key, Porter<T> porter) {
    porters.put(key, porter);
  }
}
