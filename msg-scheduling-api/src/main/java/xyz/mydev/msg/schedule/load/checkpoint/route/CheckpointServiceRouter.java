package xyz.mydev.msg.schedule.load.checkpoint.route;

import xyz.mydev.msg.common.route.Router;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;

import java.util.Set;

/**
 * @author ZSP
 */
public interface CheckpointServiceRouter extends Router<String, CheckpointService> {

  void put(CheckpointService checkpointService);

  void putIfAbsent(String tableName, CheckpointService checkpointService);

  Set<String> tableNameSet();

}