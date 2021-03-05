package xyz.mydev.msg.schedule;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.port.AbstractPorter;
import xyz.mydev.msg.schedule.port.route.PortRouter;

/**
 * @author ZSP
 */
@Slf4j
public class DefaultLocalMessageStoreEventListener<T extends StringMessage> implements LocalMessageStoreEventListener<String, T> {

  private final PortRouter<T> router;

  public DefaultLocalMessageStoreEventListener(PortRouter<T> abstractPorter) {
    this.router = abstractPorter;
  }


  @Override
  public void onEvent(LocalMessageStoreEvent<T> localMessageStoreEvent) {
    T localMessage = localMessageStoreEvent.getLocalMessage();
    String targetTableName = localMessage.getTargetTableName();
    AbstractPorter<T> porter = router.get(targetTableName);
    if (porter != null) {
      porter.put(localMessage);
    } else {
      log.warn("porter 404 for {}, please check config", targetTableName);
    }
  }
}
