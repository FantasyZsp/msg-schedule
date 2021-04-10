package xyz.mydev.msg.schedule.port;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.bean.InstantMessage;
import xyz.mydev.msg.schedule.mq.producer.MqProducer;

import javax.validation.constraints.NotNull;

/**
 * @author ZSP
 */
@Slf4j
public class DefaultInstantMessageTransferTaskFactory implements TransferTaskFactory<InstantMessage> {
  final MqProducer mqProducer;

  public DefaultInstantMessageTransferTaskFactory(MqProducer mqProducer) {
    this.mqProducer = mqProducer;
  }

  @Override
  public @NotNull Runnable newTask(InstantMessage instantMessage) {
    return () -> {
      Object transactionSendResult = mqProducer.sendWithTx(instantMessage);
      if (transactionSendResult != null) {
        log.info("send success, remove msg. msgId [{}] transactionSendResult {}", instantMessage.getId(), transactionSendResult);
      } else {
        log.error("send error, wait for retry id {}", instantMessage.getId());
      }
    };
  }
}
