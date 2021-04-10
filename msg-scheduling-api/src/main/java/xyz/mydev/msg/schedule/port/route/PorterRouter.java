package xyz.mydev.msg.schedule.port.route;

import xyz.mydev.msg.schedule.port.Porter;

import java.util.Set;

/**
 * tableName -> Porter
 *
 * @author ZSP
 */
public interface PorterRouter extends Iterable<Porter<?>> {

  <T> Porter<T> get(String targetTableName);

  void put(String key, Porter<?> val);

  int size();

  Set<String> getScheduledTables();

}
