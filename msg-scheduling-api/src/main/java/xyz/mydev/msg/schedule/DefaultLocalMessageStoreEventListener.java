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
public class DefaultLocalMessageStoreEventListener implements LocalMessageStoreEventListener<String, SerializableMessage<? extends Serializable>> {

  private final PorterRouter router;

  public DefaultLocalMessageStoreEventListener(PorterRouter abstractPorter) {
    this.router = abstractPorter;
  }

  @Override
  public void onEvent(LocalMessageStoreEvent<SerializableMessage<? extends Serializable>> localMessageStoreEvent) {

    SerializableMessage<? extends Serializable> localMessage = localMessageStoreEvent.getLocalMessage();
    String targetTableName = localMessage.getTargetTableName();
    Porter<SerializableMessage<? extends Serializable>> porter = router.get(targetTableName);
    if (porter != null) {
      porter.transfer(localMessage);
    } else {
      log.warn("porter 404 for {}, please check config", targetTableName);

    }
  }
}
