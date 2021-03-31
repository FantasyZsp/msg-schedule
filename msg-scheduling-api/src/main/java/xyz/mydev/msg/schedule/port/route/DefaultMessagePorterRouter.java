package xyz.mydev.msg.schedule.port.route;

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
  private final Map<String, Porter<?>> porters = new ConcurrentHashMap<>();

  @Override
  @SuppressWarnings("unchecked")
  public <T> Porter<T> get(String key) {
    return (Porter<T>) porters.get(key);
  }


  @Override
  public void put(String key, Porter<?> val) {
    porters.put(key, val);
  }

  public void putIfAbsent(String key, Porter<?> val) {
    porters.putIfAbsent(key, val);
  }

  @Override
  public String toString() {
    return "DefaultMessagePorterRouter{" +
      "porters=" + porters +
      '}';
  }
}
