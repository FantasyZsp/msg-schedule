package xyz.mydev.msg.schedule.load.checkpoint;

import xyz.mydev.msg.schedule.load.checkpoint.BaseCheckpointService;

/**
 * 未发送的消息检查点
 * 基于时间索引
 *
 * @author ZSP
 */
public interface CheckpointService extends BaseCheckpointService<String, String> {

}
