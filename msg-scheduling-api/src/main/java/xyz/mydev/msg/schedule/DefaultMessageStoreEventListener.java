package xyz.mydev.msg.schedule;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.bean.DelayMessage;
import xyz.mydev.msg.schedule.bean.SerializableMessage;
import xyz.mydev.msg.schedule.port.Porter;
import xyz.mydev.msg.schedule.port.route.PorterRouter;

import java.io.Serializable;

/**
 * @author ZSP
 */
@Slf4j
public class DefaultMessageStoreEventListener<T extends SerializableMessage<? extends Serializable>> implements MessageStoreEventListener<T> {

  private final PorterRouter router;

  public DefaultMessageStoreEventListener(PorterRouter router) {
    this.router = router;
  }

  @Override
  public void onEvent(T message) {
    String targetTableName = message.getTargetTableName();
    Porter<T> porter = router.get(targetTableName);
    if (porter == null) {
      log.error("porter 404 for {}, please check config", targetTableName);
      return;
    }
    transferIfPossible(message, porter);
  }

  private void transferIfPossible(T message, Porter<T> porter) {
    if (message instanceof DelayMessage) {
      DelayMessage delayMessage = (DelayMessage) message;
      if (ScheduleTimeEvaluator.shouldPutNow(delayMessage.getTime(), delayMessage.getTargetTableName())) {
        porter.transfer(message);
      } else {
        log.warn("delayMessage time at {}, need not to publish now", ((DelayMessage) message).getTime());
      }
    } else {
      porter.transfer(message);
    }
  }
}
