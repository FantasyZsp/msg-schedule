package xyz.mydev.msg.schedule.load.checkpoint.route;

import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;

import java.util.HashMap;
import java.util.Map;

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
      holder.put(tableName, checkpointService);
    }
  }

  @Override
  public void putIfAbsent(String tableName, CheckpointService checkpointService) {
    holder.putIfAbsent(tableName, checkpointService);
    checkpointService.getTableNames().add(tableName);

  }

  @Override
  public CheckpointService get(String key) {
    return holder.get(key);
  }


}
