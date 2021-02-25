package xyz.mydev.msg.schedule.load.checkpoint.route;

import xyz.mydev.msg.common.route.Router;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;

import java.util.List;
import java.util.Map;

/**
 * 表名路由到CheckpointService，一对一
 * 持有所有CheckpointService
 *
 * @author ZSP
 */
public interface CheckpointServiceRouter extends Router<String, CheckpointService> {

  @Override
  CheckpointService get(String msgTableName);

  List<CheckpointService> getCheckpointServices();

  /**
   * 获取所有被维护的表名
   */
  List<String> getMsgTableNames();

  Map<String, CheckpointService> getMap();


}
