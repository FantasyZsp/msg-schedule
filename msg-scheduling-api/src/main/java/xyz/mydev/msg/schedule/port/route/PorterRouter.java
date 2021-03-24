package xyz.mydev.msg.schedule.port.route;

import xyz.mydev.msg.common.route.Router;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.port.AbstractPorter;

/**
 * tableName -> AbstractPorter
 *
 * @author ZSP
 */
public interface PorterRouter<T extends StringMessage> extends Router<String, AbstractPorter<T>> {

  default AbstractPorter<T> resolveByMessage(T msg) {
    return get(msg.getTargetTableName());
  }

}
