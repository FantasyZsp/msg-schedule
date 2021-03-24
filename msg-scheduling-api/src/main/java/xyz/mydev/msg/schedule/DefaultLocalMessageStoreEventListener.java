package xyz.mydev.msg.schedule;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.port.Porter;
import xyz.mydev.msg.schedule.port.route.PorterRouter;

/**
 * @author ZSP
 */
@Slf4j
public class DefaultLocalMessageStoreEventListener<T extends StringMessage> implements LocalMessageStoreEventListener<String, T> {

  private final PorterRouter router;

  public DefaultLocalMessageStoreEventListener(PorterRouter abstractPorter) {
    this.router = abstractPorter;
  }

  @Override
  public void onEvent(LocalMessageStoreEvent<T> localMessageStoreEvent) {
    T localMessage = localMessageStoreEvent.getLocalMessage();
    String targetTableName = localMessage.getTargetTableName();
    Porter<? super StringMessage> porter = router.get(targetTableName);
    if (porter != null) {
      porter.transfer(localMessage);
    } else {
      log.warn("porter 404 for {}, please check config", targetTableName);
    }
  }
}
