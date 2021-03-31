package xyz.mydev.msg.schedule.mq.error.record;

import org.springframework.stereotype.Component;
import org.springframework.util.JdkIdGenerator;
import xyz.mydev.msg.schedule.IdGenerator;

/**
 * @author zhaosp
 */
@Component
public class InternalIdGenerator implements IdGenerator {
  JdkIdGenerator jdkIdGenerator = new JdkIdGenerator();

  @Override
  public String get() {
    return jdkIdGenerator.generateId().toString();
  }
}