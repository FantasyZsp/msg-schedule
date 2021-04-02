package xyz.mydev.msg.schedule.bridge.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import xyz.mydev.msg.schedule.MessageStoreEvent;

/**
 * @author ZSP
 */
public class GenericMessageStoreEvent<T> extends ApplicationEvent implements ResolvableTypeProvider, MessageStoreEvent<T> {

  private final T message;

  public GenericMessageStoreEvent(T message) {
    super(message);
    this.message = message;
  }

  @Override
  public ResolvableType getResolvableType() {
    return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getMessage()));
  }

  @Override
  public T getMessage() {
    return message;
  }
}