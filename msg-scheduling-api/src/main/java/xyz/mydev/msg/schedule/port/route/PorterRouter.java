package xyz.mydev.msg.schedule.port.route;

import xyz.mydev.msg.schedule.port.Porter;

/**
 * tableName -> AbstractPorter
 *
 * @author ZSP
 */
public interface PorterRouter {

  <T> Porter<T> get(String targetTableName);

  void put(String key, Porter<?> val);

}
