package xyz.mydev.msg.schedule;

import lombok.extern.slf4j.Slf4j;
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
  public void onEvent(MessageStoreEvent<T> messageStoreEvent) {

    T localMessage = messageStoreEvent.getMessage();
    String targetTableName = localMessage.getTargetTableName();
    Porter<T> porter = router.get(targetTableName);
    if (porter != null) {
      porter.transfer(localMessage);
    } else {
      log.error("porter 404 for {}, please check config", targetTableName);
    }
  }
}
