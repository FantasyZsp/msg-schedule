package xyz.mydev.msg.schedule.load.checkpoint.route;

import xyz.mydev.msg.common.route.Router;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;

/**
 * 表名路由到CheckpointService，一对一
 *
 * @author ZSP
 */
public interface CheckpointServiceRouter extends Router<String, CheckpointService> {
  @Override
  CheckpointService get(String msgTableName);
}
