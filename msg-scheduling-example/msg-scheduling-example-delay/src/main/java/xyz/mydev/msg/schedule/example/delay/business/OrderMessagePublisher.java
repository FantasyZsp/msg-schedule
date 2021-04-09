package xyz.mydev.msg.schedule.example.delay.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xyz.mydev.msg.schedule.MessageStoreService;
import xyz.mydev.msg.schedule.bean.StringMessage;

/**
 * @author ZSP
 */
@Component
public class OrderMessagePublisher {
  @Autowired
  private MessageStoreService messageStoreService;

  @Transactional
  public void publish(StringMessage messageEntity) {
    messageStoreService.store(messageEntity);
  }


}
