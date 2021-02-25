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

  List<String> getTableNames();

}
