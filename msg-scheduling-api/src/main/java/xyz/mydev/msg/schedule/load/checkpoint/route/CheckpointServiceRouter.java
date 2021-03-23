package xyz.mydev.msg.schedule.load.checkpoint.route;

import xyz.mydev.msg.common.route.Router;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZSP
 */
public interface CheckpointServiceRouter extends Router<String, CheckpointService> {

  Collection<CheckpointService> getCheckpointServicePool();

  void setRouteHolder(Map<String, CheckpointService> map);

  Map<String, CheckpointService> getRouteHolder();

  default void initRouter() {
    Collection<CheckpointService> checkpointServicePool = getCheckpointServicePool();
    Map<String, CheckpointService> map = new HashMap<>(checkpointServicePool.size());
    checkpointServicePool.forEach(cp -> {
      List<String> tableNames = cp.getTableNames();
      for (String tableName : tableNames) {
        map.put(tableName, cp);
      }
    });
    setRouteHolder(map);
  }

  @Override
  default CheckpointService get(String key) {
    return getRouteHolder().get(key);
  }


}
