package xyz.mydev.msg.schedule.delay.port;

import org.junit.jupiter.api.Test;
import xyz.mydev.msg.common.TableKeyPair;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.delay.infrastruction.repository.bean.DelayMessage;
import xyz.mydev.msg.schedule.port.DefaultPorter;
import xyz.mydev.msg.schedule.port.route.DefaultMessagePorterRouter;

/**
 * @author ZSP
 */
class DefaultMessagePorterRouterTest {

  @Test
  void testGeneric() {
    DefaultPorter<StringMessage> test = new DefaultPorter<>("1", null, null, null);
    DefaultPorter<DelayMessage> test2 = new DefaultPorter<>("2", null, null, null);
    DelayMessagePorter<DelayMessage> test3 = new DelayMessagePorter<>("3", null, null, null);
    DefaultMessagePorterRouter router = new DefaultMessagePorterRouter();
    router.put(TableKeyPair.of("test", StringMessage.class), test);
    router.put(TableKeyPair.of("test2", DelayMessage.class), test2);
    router.put(TableKeyPair.of("test3", DelayMessage.class), test3);

    System.out.println(router.getByKey(TableKeyPair.of("test", StringMessage.class)));
    System.out.println(test);

    System.out.println(router.getByKey(TableKeyPair.of("test2", DelayMessage.class)));
    System.out.println(test2);
    System.out.println(router.getByKey(TableKeyPair.of("test3", DelayMessage.class)));
    System.out.println(test3);

  }

}