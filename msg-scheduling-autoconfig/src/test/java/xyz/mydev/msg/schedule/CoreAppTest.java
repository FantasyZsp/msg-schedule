package xyz.mydev.msg.schedule;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import xyz.mydev.msg.schedule.bean.DelayMessage;
import xyz.mydev.msg.schedule.bean.InstantMessage;
import xyz.mydev.msg.schedule.mq.error.record.MqMessageErrorRecord;
import xyz.mydev.msg.schedule.mq.error.record.MqMessageErrorRecordDao;

import java.time.LocalDateTime;

/**
 * @author ZSP
 */
@SpringBootApplication
public class CoreAppTest {
  public static void main(String[] args) {

    new SpringApplicationBuilder(CoreAppTest.class)
      .profiles("msg-schedule")
      .web(WebApplicationType.NONE)
      .run(args);

  }

  @Bean
  public static MqMessageErrorRecordDao test() {
    return new MqMessageErrorRecordDao() {
      @Override
      public int deleteByPrimaryKey(String id) {
        return 0;
      }

      @Override
      public int insert(MqMessageErrorRecord record) {
        return 0;
      }
    };
  }


  public static class PersonMessage implements InstantMessage {

    @Override
    public String getTraceId() {
      return null;
    }

    @Override
    public String getTraceVersion() {
      return null;
    }

    @Override
    public String getBusinessId() {
      return null;
    }

    @Override
    public int getPlatform() {
      return 0;
    }

    @Override
    public String getPlatformMsgId() {
      return null;
    }

    @Override
    public String getTag() {
      return null;
    }

    @Override
    public String getId() {
      return null;
    }

    @Override
    public String getTopic() {
      return null;
    }

    @Override
    public String getPayload() {
      return null;
    }

    @Override
    public Integer getStatus() {
      return null;
    }

    @Override
    public String getTargetTableName() {
      return null;
    }

    @Override
    public Boolean isDelay() {
      return null;
    }

    @Override
    public Boolean isTx() {
      return null;
    }
  }

  public static class TmpDelayMsg implements DelayMessage {

    @Override
    public String getTraceId() {
      return null;
    }

    @Override
    public String getTraceVersion() {
      return null;
    }

    @Override
    public String getBusinessId() {
      return null;
    }

    @Override
    public int getPlatform() {
      return 0;
    }

    @Override
    public String getPlatformMsgId() {
      return null;
    }

    @Override
    public String getTag() {
      return null;
    }

    @Override
    public String getId() {
      return null;
    }

    @Override
    public String getTopic() {
      return null;
    }

    @Override
    public String getPayload() {
      return null;
    }

    @Override
    public Integer getStatus() {
      return null;
    }

    @Override
    public String getTargetTableName() {
      return null;
    }

    @Override
    public Boolean isDelay() {
      return null;
    }

    @Override
    public Boolean isTx() {
      return null;
    }

    @Override
    public LocalDateTime getTime() {
      return null;
    }
  }
}
