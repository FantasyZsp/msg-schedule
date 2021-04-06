package xyz.mydev.msg.schedule;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import xyz.mydev.msg.schedule.bean.DelayMessage;
import xyz.mydev.msg.schedule.bean.InstantMessage;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;
import xyz.mydev.msg.schedule.infrastruction.repository.route.DefaultMessageRepositoryRouter;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;
import xyz.mydev.msg.schedule.mq.error.record.MqMessageErrorRecord;
import xyz.mydev.msg.schedule.mq.error.record.MqMessageErrorRecordDao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

  @Bean
  @ConditionalOnMissingBean
  public MessageRepositoryRouter messageRepositoryRouter() {
    DefaultMessageRepositoryRouter router = new DefaultMessageRepositoryRouter();
    router.put("delayMsgTableA", new TmpMessageRepository("delayMsgTableA"));
    router.put("txMsgTableB", new TmpMessageRepository("txMsgTableB"));
    router.put("txMsgTableC", new TmpMessageRepository("txMsgTableC"));
    router.init();
    return router;
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

  public static class TmpMessageRepository implements MessageRepository<StringMessage> {

    String tableName;

    public TmpMessageRepository(String tableName) {
      this.tableName = tableName;
    }

    @Override
    public String getTableName() {
      return tableName;
    }

    @Override
    public StringMessage selectById(String s) {
      return null;
    }

    @Override
    public int insert(StringMessage entity) {
      return 0;
    }

    @Override
    public boolean updateStatus(String id, int status) {
      return false;
    }

    @Override
    public boolean updateToSent(String s) {
      return false;
    }

    @Override
    public List<StringMessage> findWillSendBetween(LocalDateTime startTime, LocalDateTime endTime) {
      return new ArrayList<>();
    }

    @Override
    public Optional<LocalDateTime> findCheckpoint() {
      return Optional.empty();
    }

    @Override
    public Optional<LocalDateTime> findNextCheckpointAfter(LocalDateTime oldCheckPoint) {
      return Optional.empty();
    }
  }
}
