package xyz.mydev.msg.common;

import java.util.Objects;

/**
 * @author ZSP
 */
public class TableKeyPair<T> {
  final String tableName;
  final Class<T> targetClass;

  private TableKeyPair(String tableName, Class<T> targetClass) {
    this.tableName = tableName;
    this.targetClass = targetClass;
  }

  public static <T> TableKeyPair<T> of(String tableName, Class<T> targetClass) {
    return new TableKeyPair<>(tableName, targetClass);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TableKeyPair<?> tableKeyPair = (TableKeyPair<?>) o;
    return tableName.equals(tableKeyPair.tableName) && targetClass.equals(tableKeyPair.targetClass);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tableName, targetClass);
  }

  public String getTableName() {
    return tableName;
  }

  public Class<T> getTargetClass() {
    return targetClass;
  }
}