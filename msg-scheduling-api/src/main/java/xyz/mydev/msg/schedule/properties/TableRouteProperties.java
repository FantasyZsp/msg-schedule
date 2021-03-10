package xyz.mydev.msg.schedule.properties;

import lombok.Data;

import java.util.Map;

/**
 * @author zhaosp
 */
@Data
public class TableRouteProperties {

  private boolean loadFromRepositoryApi = true;
  /**
   * tableName -> properties
   */
  private Map<String, TableScheduleProperties> tables;


}
