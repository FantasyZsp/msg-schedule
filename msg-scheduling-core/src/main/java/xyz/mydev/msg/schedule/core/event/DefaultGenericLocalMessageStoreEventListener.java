package xyz.mydev.msg.schedule.core.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.event.TransactionalEventListener;
import xyz.mydev.msg.schedule.LocalMessageStoreEventListener;
import xyz.mydev.msg.schedule.bean.StringMessage;

/**
 * @author ZSP
 */
@Slf4j
public class DefaultGenericLocalMessageStoreEventListener {

  private final LocalMessageStoreEventListener<String, StringMessage> localMessageStoreEventListener;

  public DefaultGenericLocalMessageStoreEventListener(LocalMessageStoreEventListener<String, StringMessage> localMessageStoreEventListener) {
    this.localMessageStoreEventListener = localMessageStoreEventListener;
  }

  /**
   * 异步化依赖于后续流程是否启用线程池
   */
  @TransactionalEventListener
  public void onGenericLocalMessageStoreEvent(GenericLocalMessageStoreEvent<StringMessage> event) {
    log.info("receive info : {}", event.getLocalMessage());
    localMessageStoreEventListener.onEvent(event);
  }
}
