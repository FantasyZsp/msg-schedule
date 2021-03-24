package xyz.mydev.msg.schedule.port.route;

import xyz.mydev.msg.common.route.Router;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.port.Porter;

/**
 * tableName -> AbstractPorter
 *
 * @author ZSP
 */
public interface PorterRouter extends Router<String, Porter<? super StringMessage>> {

  default <E extends StringMessage> Porter<? super StringMessage> resolveByMessage(E msg) {
    return get(msg.getTargetTableName());
  }

}
