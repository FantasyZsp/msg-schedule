package xyz.mydev.msg.schedule.load.checkpoint.route;

import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author ZSP
 */
public class DefaultCheckpointServiceRouter implements CheckpointServiceRouter {

  /**
   * tableName -> CheckpointService
   */
  private final Map<String, CheckpointService> holder = new HashMap<>();

  @Override
  public void put(CheckpointService checkpointService) {
    for (String tableName : checkpointService.getTableNames()) {
      holder.putIfAbsent(tableName, checkpointService);
    }
  }

  @Override
  public void putIfAbsent(String tableName, CheckpointService checkpointService) {
    holder.putIfAbsent(tableName, checkpointService);
    checkpointService.getTableNames().add(tableName);
  }

  @Override
  public Set<String> tableNameSet() {
    return Set.copyOf(holder.keySet());
  }

  @Override
  public int size() {
    return holder.size();
  }

  @Override
  public CheckpointService get(String key) {
    return holder.get(key);
  }


  @Override
  public Iterator<CheckpointService> iterator() {
    return holder.values().iterator();
  }
}
