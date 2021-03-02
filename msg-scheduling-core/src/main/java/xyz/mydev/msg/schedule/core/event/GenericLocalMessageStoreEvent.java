package xyz.mydev.msg.schedule.core.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import xyz.mydev.msg.schedule.LocalMessageStoreEvent;

/**
 * @author ZSP
 */
public class GenericLocalMessageStoreEvent<T> extends ApplicationEvent implements ResolvableTypeProvider, LocalMessageStoreEvent<T> {

  private final T localMessage;

  public GenericLocalMessageStoreEvent(T localMessage) {
    super(localMessage);
    this.localMessage = localMessage;
  }

  @Override
  public ResolvableType getResolvableType() {
    return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getLocalMessage()));
  }

  @Override
  public T getLocalMessage() {
    return localMessage;
  }
}