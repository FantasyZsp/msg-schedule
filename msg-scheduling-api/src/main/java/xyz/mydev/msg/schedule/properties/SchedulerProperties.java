package xyz.mydev.msg.schedule.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.common.DelayMessageTag;
import xyz.mydev.msg.schedule.bean.BaseMessage;

import java.util.Map;
import java.util.Objects;

/**
 * @author zhaosp
 */
@Getter
@Setter
@ToString
@Slf4j
public class SchedulerProperties {

  private static final int DEFAULT_CHECKPOINT_INTERVALS = 15;
  private static final int DEFAULT_LOAD_INTERVALS = 30;

  private boolean enable;
  private ExecutorProperties scheduleExecutor;
  private ExecutorProperties portExecutor;
  private ExecutorProperties checkpointExecutor;

  private TableScheduleProperties defaultScheduleInterval;

  private TableRouteProperties route;

  public void init() {

    if (!enable) {
      log.warn("scheduler is disable.");
    }

    log.warn("scheduler is enable, starting to config routes");

    Objects.requireNonNull(route);

    if (route.isLoadFromRepositoryApi()) {
      throw new UnsupportedOperationException();
    }

    // 从配置中加载
    // 检查必要的配置
    initDefault(defaultScheduleInterval);


    TableRouteProperties.TableConfigProperties tables = route.getTables();

    initRouteTables(tables);
//    initExecutorProperties(); // TODO 考察实际的应用方式

  }

  /**
   * 初始化执行器线程数的配置
   */
  private void initExecutorProperties() {


    /**
     * 未配置时按照实际调度的表的个数进行初始化
     */
    if (checkpointExecutor == null) {
      checkpointExecutor = new ExecutorProperties();
//      checkpointExecutor.setMaxThread(1); // TODO
    }

  }

  private void initRouteTables(TableRouteProperties.TableConfigProperties tables) {
    // init delay
    Map<String, TableScheduleProperties> delay = tables.getDelay();
    if (delay != null && delay.size() > 0) {
      for (Map.Entry<String, TableScheduleProperties> delayConfigEntry : delay.entrySet()) {
        String delayTableName = delayConfigEntry.getKey();
        TableScheduleProperties delayTableProperty = delayConfigEntry.getValue();
        initTableScheduleProperty(delayTableName, delayTableProperty, defaultScheduleInterval, true);
      }
    }

    // init instant
    Map<String, TableScheduleProperties> instant = tables.getInstant();
    if (instant != null && instant.size() > 0) {
      for (Map.Entry<String, TableScheduleProperties> delayConfigEntry : instant.entrySet()) {
        String delayTableName = delayConfigEntry.getKey();
        TableScheduleProperties delayTableProperty = delayConfigEntry.getValue();
        initTableScheduleProperty(delayTableName, delayTableProperty, defaultScheduleInterval, false);
      }
    }
  }

  private void initDefault(TableScheduleProperties defaultScheduleInterval) {
    if (defaultScheduleInterval.getCheckpointInterval() == null) {
      defaultScheduleInterval.setCheckpointInterval(DEFAULT_CHECKPOINT_INTERVALS);
    }

    if (defaultScheduleInterval.getLoadInterval() == null) {
      defaultScheduleInterval.setLoadInterval(DEFAULT_LOAD_INTERVALS);
    }

  }

  public void initTableScheduleProperty(String tableName, TableScheduleProperties tableScheduleProperties, TableScheduleProperties defaultScheduleInterval, boolean isDelay) {

    tableScheduleProperties.setTableName(tableName);
    tableScheduleProperties.setIsDelay(isDelay);
    Objects.requireNonNull(tableScheduleProperties.getTableEntityClass(), tableName + " TableEntityClass must be not null");
    boolean assignableFrom = BaseMessage.class.isAssignableFrom(tableScheduleProperties.getTableEntityClass());
    if (!assignableFrom) {
      throw new IllegalArgumentException("invalid msg class");
    }

    if (isDelay) {
      boolean extendsDelayMessageTag = DelayMessageTag.class.isAssignableFrom(tableScheduleProperties.getTableEntityClass());
      if (!extendsDelayMessageTag) {
        throw new IllegalArgumentException("delay msg class must extends DelayMessageTag");
      }
    }

    if (tableScheduleProperties.getUseDefaultConfig()) {
      tableScheduleProperties.setCheckpointInterval(defaultScheduleInterval.getCheckpointInterval());
      tableScheduleProperties.setLoadInterval(defaultScheduleInterval.getLoadInterval());
    }

    Objects.requireNonNull(tableScheduleProperties.getCheckpointInterval());
    Objects.requireNonNull(tableScheduleProperties.getLoadInterval());
  }

}
