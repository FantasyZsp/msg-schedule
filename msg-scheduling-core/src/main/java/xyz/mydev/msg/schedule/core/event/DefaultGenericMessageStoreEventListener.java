package xyz.mydev.msg.schedule.core.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.event.TransactionalEventListener;
import xyz.mydev.msg.schedule.MessageStoreEventListener;
import xyz.mydev.msg.schedule.bean.StringMessage;

/**
 * @author ZSP
 */
@Slf4j
public class DefaultGenericMessageStoreEventListener {

  private final MessageStoreEventListener<StringMessage> messageStoreEventListener;

  public DefaultGenericMessageStoreEventListener(MessageStoreEventListener<StringMessage> messageStoreEventListener) {
    this.messageStoreEventListener = messageStoreEventListener;
  }

  /**
   * 异步化依赖于后续流程是否启用线程池
   */
  @TransactionalEventListener
  public void onGenericMessageStoreEvent(GenericMessageStoreEvent<StringMessage> event) {
    log.info("receive info : {}", event.getMessage());
    messageStoreEventListener.onEvent(event);
  }
}
