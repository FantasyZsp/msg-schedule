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

  @PostMapping
  public void saveAndSend(@RequestBody Order order) throws Exception {
    orderService.saveAndSend(order);
  }

}
