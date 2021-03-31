package xyz.mydev.msg.schedule.load.checkpoint.route;

import xyz.mydev.msg.common.route.Router;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;

import java.util.Set;

/**
 * @author ZSP
 */
public interface CheckpointServiceRouter extends Router<String, CheckpointService>, Iterable<CheckpointService> {

  /**
   * 将检查服务及其负责调度的表注册到 router中
   * 实现方可以考虑对重复的处理
   *
   * @param checkpointService 检查服务
   */
  void put(CheckpointService checkpointService);

  void putIfAbsent(String tableName, CheckpointService checkpointService);

  Set<String> tableNameSet();

  int size();

}
