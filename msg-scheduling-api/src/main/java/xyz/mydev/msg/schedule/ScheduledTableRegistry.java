package xyz.mydev.msg.schedule;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 注册途径：
 * <p>
 * 1. bean组件装配
 * 一般需要使用IOC框架注入调度组件，而后为调度的表提供一组调度参数和元信息供调度框架使用</p>
 * 注册时机一般在自定义组件的初始化方法中
 * 2. yml装配
 * 在yml中配置的条目，在自动装配完毕后，会注册到此。
 * <p>
 * TODO 获取这个注册中心可以注册任何组件，而不必使用Router
 *
 * @author ZSP
 */
public class ScheduledTableRegistry implements Iterable<ScheduledTableRegistry.TableSchedulePropertiesWrapper> {

  public static final int REGISTER_WAY_BEAN = 1;
  public static final int REGISTER_WAY_YML = 2;


  private static final Map<String, TableSchedulePropertiesWrapper> REGISTRY = new ConcurrentHashMap<>();

  public static void registerTableByConfig(String tableName, TableScheduleProperties tableScheduleProperties) {
    REGISTRY.put(tableName, TableSchedulePropertiesWrapper.adapt(tableScheduleProperties.validate(), REGISTER_WAY_YML));
  }

  public static void registerTableByBean(String tableName, TableScheduleProperties tableScheduleProperties) {
    REGISTRY.put(tableName, TableSchedulePropertiesWrapper.adapt(tableScheduleProperties.validate(), REGISTER_WAY_BEAN));
  }

  public static void removeTable(String tableName) {
    REGISTRY.remove(tableName);
  }

  public static TableScheduleProperties getTableProperties(String tableName) {
    return Objects.requireNonNull(REGISTRY.get(tableName));
  }

  public static int getTableLoadIntervalMinutes(String tableName) {
    return ScheduledTableRegistry.getTableProperties(tableName).getLoadInterval();
  }

  public static boolean isDelayTable(String tableName) {
    return ScheduledTableRegistry.getTableProperties(tableName).getIsDelay();
  }

  public static class TableSchedulePropertiesWrapper extends TableScheduleProperties {
    /**
     * 1. bean组件装配 2.yml配置
     */
    private int registerWay;

    public int getRegisterWay() {
      return registerWay;
    }

    public void setRegisterWay(int registerWay) {
      this.registerWay = registerWay;
    }

    static TableSchedulePropertiesWrapper adapt(TableScheduleProperties source, int registerWay) {
      TableSchedulePropertiesWrapper wrapper = new TableSchedulePropertiesWrapper();
      wrapper.registerWay = registerWay;
      wrapper.setCheckpointInterval(source.getCheckpointInterval());
      wrapper.setIsDelay(source.getIsDelay());
      wrapper.setLoadInterval(source.getLoadInterval());
      wrapper.setTableEntityClass(source.getTableEntityClass());
      wrapper.setTableName(source.getTableName());
      wrapper.setUseDefaultConfig(source.getUseDefaultConfig());
      return wrapper;
    }
  }

  @Override
  public Iterator<TableSchedulePropertiesWrapper> iterator() {
    return REGISTRY.values().iterator();
  }
}
