package xyz.mydev.msg.schedule.example.delay.business.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZSP
 */
@RestController
@RequestMapping("/order")
public class OrderController {

  @Autowired
  private OrderService orderService;

  @PostMapping("/delay")
  public void saveAndSendWithDelay(@RequestBody Order order) throws Exception {
    orderService.saveAndSend(order, true, false);
  }

  @PostMapping("/instant")
  public void saveAndSendWithInstant(@RequestBody Order order) throws Exception {
    orderService.saveAndSend(order, false, true);
  }

  @PostMapping("/both")
  public void saveAndSendWithBoth(@RequestBody Order order) throws Exception {
    orderService.saveAndSend(order, true, true);
  }

}
