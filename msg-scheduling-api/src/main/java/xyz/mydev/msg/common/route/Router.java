package xyz.mydev.msg.common.route;

/**
 * @author ZSP
 */
public interface Router<K, V> {

  V get(K key);

}
