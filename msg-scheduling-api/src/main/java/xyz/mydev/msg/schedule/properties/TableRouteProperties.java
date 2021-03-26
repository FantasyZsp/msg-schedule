package xyz.mydev.msg.schedule.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author zhaosp
 */
@Data
public class TableRouteProperties {

  /**
   * 当配置为true时，将会从当前类路径或者容器中收集实现了 MessageRepository 的接口，其关联的信息作为调度表信息
   */
  private boolean loadFromRepositoryApi = true;

  private TableConfigProperties tables;


  @Getter
  @Setter
  public static class TableConfigProperties {
    /**
     * tableName -> properties
     */
    Map<String, TableScheduleProperties> delay;
    Map<String, TableScheduleProperties> instant;

  }


}
