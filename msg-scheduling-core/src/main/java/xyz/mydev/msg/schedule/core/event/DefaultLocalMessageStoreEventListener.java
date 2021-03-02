package xyz.mydev.msg.schedule.core.event;

import xyz.mydev.msg.schedule.LocalMessageStoreEvent;
import xyz.mydev.msg.schedule.LocalMessageStoreEventListener;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.port.AbstractPorter;

/**
 * @author ZSP
 */
public class DefaultLocalMessageStoreEventListener<T extends StringMessage> implements LocalMessageStoreEventListener<String, T> {

  private final AbstractPorter<T> porter;

  public DefaultLocalMessageStoreEventListener(AbstractPorter<T> abstractPorter) {
    this.porter = abstractPorter;
  }


  @Override
  public void onEvent(LocalMessageStoreEvent<T> localMessageStoreEvent) {
    T localMessage = localMessageStoreEvent.getLocalMessage();
    // TODO 调度策略与路由如何处理
    porter.port(localMessage);
  }
}
