package xyz.mydev.msg.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * JSON操作工具类
 *
 * @author zhao
 */
public class JsonUtil {
  private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);
  public static ObjectMapper objectMapper = new ObjectMapper();

  static {
    // 将对象的所有字段全部序列化
    objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    // 忽略空bean转json的错误
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    // 忽略在json中存在但是在java对象中不存在对应属性的情况，防止错误
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // 配置支持java8 time
    objectMapper.registerModule(new JavaTimeModule());

    // 转换timestamp形式，默认为时间戳，false为instant
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    // 配置时间戳的单位
    objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

    // map排序
    objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, false);
  }

  /**
   * Object2String
   *
   * @return java.lang.String
   * @author ZSP
   * @date 2018/8/1
   */
  public static <T> String obj2String(T obj) {
    if (obj == null) {
      return null;
    }
    try {
      return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      log.warn("Parse object to String error", e);
      return null;
    }
  }

  public static <T> String obj2StringPretty(T obj) {
    if (obj == null) {
      return null;
    }
    try {
      return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    } catch (Exception e) {
      log.warn("Parse object to String error", e);
      return null;
    }
  }

  /**
   * 简单对象反序列化
   *
   * @param str, clazz
   * @return T
   * @author ZSP
   * @date 2018/8/1
   */
  @SuppressWarnings("unchecked")
  public static <T> T string2Obj(String str, Class<T> clazz) {
    if (StringUtils.isEmpty(str) || clazz == null) {
      return null;
    }
    try {
      return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
    } catch (Exception e) {
      log.warn("Parse String to object error", e);
      return null;
    }

  }

  /**
   * 字符串反序列化为组合对象。传入类型和返回类型一致为T。
   *
   * @param str           字符串
   * @param typeReference 转换的类型
   * @return T
   * @author ZSP
   * @date 2018/8/1
   */
  @SuppressWarnings("unchecked")
  public static <T> T string2Obj(String str, TypeReference<T> typeReference) {
    if (StringUtils.isEmpty(str) || typeReference == null) {
      return null;
    }
    try {
      return (typeReference.getType().equals(String.class) ? (T) str : objectMapper.readValue(str, typeReference));
    } catch (Exception e) {
      log.warn("Parse String to objectT error", e);
      return null;
    }

  }

  public static <T> T string2Obj(String str, Class<?> collectionClass, Class<?>... elementClasses) {
    if (StringUtils.isEmpty(str) || collectionClass == null) {
      return null;
    }
    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    try {
      return objectMapper.readValue(str, javaType);
    } catch (Exception e) {
      log.warn("Parse String to object error", e);
      return null;
    }

  }

}
