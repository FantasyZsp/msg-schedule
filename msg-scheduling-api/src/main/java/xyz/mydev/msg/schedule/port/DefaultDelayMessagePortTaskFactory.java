package xyz.mydev.msg.schedule.port;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.bean.DelayMessage;
import xyz.mydev.msg.schedule.mq.producer.MqProducer;

import javax.validation.constraints.NotNull;

/**
 * @author ZSP
 */
@Slf4j
public class DefaultDelayMessagePortTaskFactory implements PortTaskFactory<DelayMessage> {
  final MqProducer mqProducer;
  final TransferQueue<DelayMessage> transferQueue;

  public DefaultDelayMessagePortTaskFactory(MqProducer mqProducer,
                                            TransferQueue<DelayMessage> transferQueue) {
    this.mqProducer = mqProducer;
    this.transferQueue = transferQueue;
  }

  @Override
  public @NotNull Runnable newTask(DelayMessage delayMessage) {
    return () -> {
      Object transactionSendResult = mqProducer.sendWithTx(delayMessage);

      if (transactionSendResult != null) {
        boolean remove = transferQueue.remove(delayMessage);
        log.info("send success, remove msg. msgId [{}] transactionSendResult {}", delayMessage.getId(), transactionSendResult);
        if (!remove) {
          log.warn("remove false , maybe already not exists");
        }
      } else {
        log.error("send error, waiter for retry id {}", delayMessage.getId());
      }

    };
  }
}
