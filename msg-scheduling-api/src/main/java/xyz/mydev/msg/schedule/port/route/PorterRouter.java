package xyz.mydev.msg.schedule.port.route;

import xyz.mydev.msg.common.TableKeyPair;
import xyz.mydev.msg.schedule.bean.SerializableMessage;
import xyz.mydev.msg.schedule.port.Porter;

import java.io.Serializable;

/**
 * tableName -> AbstractPorter
 *
 * @author ZSP
 */
public interface PorterRouter {

  @SuppressWarnings("unchecked")
  default <T extends SerializableMessage<? extends Serializable>> Porter<T> resolveByMessage(T msg) {
    return (Porter<T>) get(TableKeyPair.of(msg.getTargetTableName(), msg.getClass()));
  }

  <T> Porter<T> get(TableKeyPair<T> of);

  <T> void put(TableKeyPair<T> key, Porter<T> val);

  void putAny(TableKeyPair<?> key, Porter<?> val);


  default <T extends SerializableMessage<? extends Serializable>> Porter<T> getByKey(TableKeyPair<T> keyPair) {
    return get(keyPair);
  }


}
