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
public class DefaultMessagePorterRouter implements PorterRouter {

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

  @Override
  public void putAny(TableKeyPair<?> key, Porter<?> val) {
    porters.put(key, val);
  }

  public void putIfAbsent(TableKeyPair<?> key, Porter<?> val) {
    porters.putIfAbsent(key, val);
  }

  @Override
  public String toString() {
    return "DefaultMessagePorterRouter{" +
      "porters=" + porters +
      '}';
  }
}
