package xyz.mydev.msg.schedule.port.route;

import xyz.mydev.msg.common.route.Router;
import xyz.mydev.msg.schedule.bean.SerializableMessage;
import xyz.mydev.msg.schedule.port.Porter;

import java.io.Serializable;

/**
 * tableName -> AbstractPorter
 *
 * @author ZSP
 */
public interface PorterRouter extends Router<String, Porter<SerializableMessage<? extends Serializable>>> {

  default <E extends SerializableMessage<? extends Serializable>> Porter<? extends SerializableMessage<? extends Serializable>> resolveByMessage(E msg) {
    return get(msg.getTargetTableName());
  }
}
