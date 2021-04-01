package xyz.mydev.msg.schedule.autoconfig.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import xyz.mydev.msg.schedule.ScheduledTableRegistry;
import xyz.mydev.msg.schedule.TableScheduleProperties;
import xyz.mydev.msg.schedule.bean.BaseMessage;
import xyz.mydev.msg.schedule.bean.DelayMessage;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author zhaosp
 */
@Getter
@Setter
@ToString
@Slf4j
@ConfigurationProperties(prefix = "msg-schedule.scheduler")
public class SchedulerProperties {

  private static final int DEFAULT_CHECKPOINT_INTERVALS = 15;
  private static final int DEFAULT_LOAD_INTERVALS = 30;

  private boolean enable;
  @NestedConfigurationProperty
  private ExecutorProperties scheduleExecutor;
  @NestedConfigurationProperty
  private ExecutorProperties checkpointExecutor;

  @NestedConfigurationProperty
  private TableScheduleProperties defaultScheduleInterval = new TableScheduleProperties();

  @NestedConfigurationProperty
  private TableRouteProperties route = new TableRouteProperties();

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
    initExecutorProperties();

  }

  /**
   * 初始化执行器线程数的配置
   */
  private void initExecutorProperties() {
    // 未配置时按照实际调度的表的个数进行初始化
    if (checkpointExecutor == null) {
      checkpointExecutor = new ExecutorProperties();
    }

    if (scheduleExecutor == null) {
      scheduleExecutor = new ExecutorProperties();
    }

  }

  private void initRouteTables(TableRouteProperties.TableConfigProperties tables) {
    // init delay
    Map<String, TableScheduleProperties> delay = tables.getDelay();
    if (MapUtils.isNotEmpty(delay)) {
      for (Map.Entry<String, TableScheduleProperties> delayConfigEntry : delay.entrySet()) {
        String delayTableName = delayConfigEntry.getKey();
        TableScheduleProperties delayTableProperty = delayConfigEntry.getValue();
        initTableScheduleProperty(delayTableName, delayTableProperty, defaultScheduleInterval, true);
      }
    }

    // init instant
    Map<String, TableScheduleProperties> instant = tables.getInstant();
    if (MapUtils.isNotEmpty(instant)) {
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
      boolean extendsDelayMessageTag = DelayMessage.class.isAssignableFrom(tableScheduleProperties.getTableEntityClass());
      if (!extendsDelayMessageTag) {
        throw new IllegalArgumentException("delay msg class must extends DelayMessage");
      }
    }

    if (tableScheduleProperties.getUseDefaultConfig()) {
      tableScheduleProperties.setCheckpointInterval(defaultScheduleInterval.getCheckpointInterval());
      tableScheduleProperties.setLoadInterval(defaultScheduleInterval.getLoadInterval());
    }

    Objects.requireNonNull(tableScheduleProperties.getCheckpointInterval());
    Objects.requireNonNull(tableScheduleProperties.getLoadInterval());
    // register
    ScheduledTableRegistry.registerTableByConfig(tableName, tableScheduleProperties);
  }

  public Set<String> scheduledTableNames() {
    Set<String> tableNameSet = new HashSet<>();
    tableNameSet.addAll(route.getTables().delay.keySet());
    tableNameSet.addAll(route.getTables().instant.keySet());
    return tableNameSet;
  }

  public Set<String> getDelayTableNames() {
    return new HashSet<>(route.getTables().delay.keySet());
  }

  public Set<String> getInstantTableNames() {
    return new HashSet<>(route.getTables().instant.keySet());
  }

}
