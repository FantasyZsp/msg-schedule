package xyz.mydev.msg.common.route;

/**
 * @author ZSP
 */
public interface GenericRouter {

  <K, V> V get(K key);

  <K, V> void put(K key, V val);


}
