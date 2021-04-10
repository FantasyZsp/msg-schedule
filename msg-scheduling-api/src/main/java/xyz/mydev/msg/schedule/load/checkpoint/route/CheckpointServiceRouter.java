package xyz.mydev.msg.schedule.load.checkpoint.route;

import xyz.mydev.msg.common.route.Router;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;

import java.util.Set;

/**
 * 内置注册途径
 * 1. 用户自定义CheckpointService，附带tableNameSet(优先级最高，会覆盖 2 的配置)
 * 2. 外部化配置调度表 和 用户自定义porter指定的表
 *
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
