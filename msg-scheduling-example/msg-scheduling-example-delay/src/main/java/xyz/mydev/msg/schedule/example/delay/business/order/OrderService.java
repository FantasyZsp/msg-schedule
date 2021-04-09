package xyz.mydev.msg.schedule.example.delay.business.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.mydev.msg.common.Constants;
import xyz.mydev.msg.common.util.JsonUtil;
import xyz.mydev.msg.schedule.IdGenerator;
import xyz.mydev.msg.schedule.example.delay.business.OrderMessagePublisher;
import xyz.mydev.msg.schedule.example.delay.repository.LocalDelayMessage;

import java.time.LocalDateTime;

/**
 * @author ZSP
 */
@Service
public class OrderService {
  @Autowired
  private OrderMessagePublisher orderMessagePublisher;
  @Autowired
  private OrderMapper orderMapper;
  @Autowired
  private IdGenerator idGenerator;

  @Transactional
  public void saveAndSend(Order order) throws Exception {
    order.setId(idGenerator.get());
    orderMapper.insert(order);

    String payload = JsonUtil.obj2String(order);
    LocalDelayMessage localDelayMessage = LocalDelayMessage.of(idGenerator.get(), "exampleOrder", "*", 1, order.getId(), payload, LocalDateTime.now().plusSeconds(10));

    // 下面的内容不需要业务设置
    localDelayMessage.setPlatform(Constants.MqPlatform.ROCKETMQ); // set by platformInfoProvider impl
    localDelayMessage.setPlatformMsgId("xxx"); // set by platformInfoProvider impl
    localDelayMessage.setTraceId("get from provider"); // set by platformInfoProvider impl
    localDelayMessage.setTraceVersion("get from provider"); // set by platformInfoProvider impl

    orderMessagePublisher.publish(localDelayMessage);


  }

}
