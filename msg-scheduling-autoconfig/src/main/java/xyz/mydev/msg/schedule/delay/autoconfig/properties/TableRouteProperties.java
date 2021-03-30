package xyz.mydev.msg.schedule.delay.autoconfig.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaosp
 */
@Getter
@Setter
@ToString
public class TableRouteProperties {

  /**
   * 当配置为true时，将会从当前类路径或者容器中收集实现了 MessageRepository 的接口，其关联的信息作为调度表信息
   */
  private boolean loadFromRepositoryApi = false;

  private TableConfigProperties tables = new TableConfigProperties();


  @Getter
  @Setter
  public static class TableConfigProperties {
    /**
     * tableName -> properties
     */
    Map<String, TableScheduleProperties> delay = new HashMap<>();
    Map<String, TableScheduleProperties> instant = new HashMap<>();
  }


}
