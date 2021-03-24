package xyz.mydev.msg.schedule.delay.port;

import lombok.Data;
import xyz.mydev.msg.schedule.delay.infrastruction.repository.bean.DelayMessage;
import xyz.mydev.msg.schedule.port.AbstractPorter;
import xyz.mydev.msg.schedule.port.route.PorterRouter;

import java.util.Collection;

/**
 * @author ZSP
 */
@Data
public class DelayMessagePorterRouter implements PorterRouter<DelayMessage> {

  private Collection<AbstractPorter<DelayMessage>> abstractPorters;


  @Override
  public AbstractPorter<DelayMessage> get(String key) {
    return null;
  }

}
