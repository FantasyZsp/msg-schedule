package xyz.mydev.msg.schedule.port.route;

import xyz.mydev.msg.schedule.port.Porter;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
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

  @Override
  public int size() {
    return porters.size();
  }

  @Override
  public Set<String> getScheduledTables() {
    return porters.keySet();
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

  @Override
  public Iterator<Porter<?>> iterator() {
    return porters.values().iterator();
  }
}
