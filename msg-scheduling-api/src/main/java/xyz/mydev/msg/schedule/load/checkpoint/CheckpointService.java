package xyz.mydev.msg.schedule.load.checkpoint;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 未发送的消息检查点
 * 基于时间索引
 *
 * @author ZSP
 */
public interface CheckpointService extends BaseCheckpointService<String, LocalDateTime> {

  /**
   * 检查点服务负责维护的表名
   * 外部通过表名获取表的仓储服务
   */
  List<String> getTableNames();

}
