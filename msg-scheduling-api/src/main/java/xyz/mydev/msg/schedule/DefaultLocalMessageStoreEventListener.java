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
public class DefaultLocalMessageStoreEventListener<T extends SerializableMessage<? extends Serializable>>
  implements LocalMessageStoreEventListener<T> {

  private final PorterRouter router;

  public DefaultLocalMessageStoreEventListener(PorterRouter router) {
    this.router = router;
  }

  @Override
  public void onEvent(LocalMessageStoreEvent<T> localMessageStoreEvent) {

    T localMessage = localMessageStoreEvent.getLocalMessage();
    String targetTableName = localMessage.getTargetTableName();
    Porter<T> porter = router.get(targetTableName);
    if (porter != null) {
      porter.transfer(localMessage);
    } else {
      log.warn("porter 404 for {}, please check config", targetTableName);


    }
  }
}
