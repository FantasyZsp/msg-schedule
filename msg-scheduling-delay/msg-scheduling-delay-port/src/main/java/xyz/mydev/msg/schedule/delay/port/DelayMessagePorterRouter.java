package xyz.mydev.msg.schedule.delay.port;

import xyz.mydev.msg.common.TableKeyPair;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.delay.infrastruction.repository.bean.DelayMessage;
import xyz.mydev.msg.schedule.port.DefaultPorter;
import xyz.mydev.msg.schedule.port.Porter;
import xyz.mydev.msg.schedule.port.route.PorterRouter;

import java.util.HashMap;
import java.util.Map;

/**
 * 依赖外部化配置初始化所有的porter
 *
 * @author ZSP
 */

public class DelayMessagePorterRouter implements PorterRouter {


  /**
   * tableKeyPair -> porter
   */
  private final Map<TableKeyPair<?>, Porter<?>> porters = new HashMap<>();


  public static void main(String[] args) {

    DefaultPorter<StringMessage> test = new DefaultPorter<>("1", null, null, null);
    DefaultPorter<DelayMessage> test2 = new DefaultPorter<>("2", null, null, null);
    DelayMessagePorter<DelayMessage> test3 = new DelayMessagePorter<>("3", null, null, null);
    DelayMessagePorterRouter router = new DelayMessagePorterRouter();
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


  @Override
  @SuppressWarnings("unchecked")
  public <T> Porter<T> get(TableKeyPair<T> key) {
    return (Porter<T>) porters.get(key);
  }

  public <T> void put(TableKeyPair<T> key, Porter<T> porter) {
    porters.put(key, porter);
  }
}
