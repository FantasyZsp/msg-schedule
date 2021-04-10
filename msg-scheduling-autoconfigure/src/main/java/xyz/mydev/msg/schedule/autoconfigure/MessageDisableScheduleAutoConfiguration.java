package xyz.mydev.msg.schedule.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.mydev.msg.schedule.MessageStoreEvent;
import xyz.mydev.msg.schedule.MessageStoreEventPublisher;
import xyz.mydev.msg.schedule.MessageStoreService;
import xyz.mydev.msg.schedule.bridge.store.DefaultMessageStoreService;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;
import xyz.mydev.msg.schedule.load.MessageLoader;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 为外部组件正常工作提供必要支持
 *
 * @author ZSP
 */
@Configuration
@AutoConfigureBefore({MessageScheduleAutoConfiguration.class})
@ConditionalOnProperty(value = "msg-schedule.scheduler.enable", havingValue = "false", matchIfMissing = true)
public class MessageDisableScheduleAutoConfiguration {

  public MessageDisableScheduleAutoConfiguration() {
    log.warn("AutoConfiguration for disableSchedule! You will be disable to schedule msg, but msg will also store in your repository");
  }

  private static final Logger log = LoggerFactory.getLogger(MessageDisableScheduleAutoConfiguration.class);

  @Bean
  @ConditionalOnMissingBean
  public MessageLoader messageLoader() {
    return new NoMessageLoader();
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageStoreEventPublisher messageStoreEventPublisher() {
    return new NoMessageStoreEventPublisher();
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageStoreService messageStoreService(MessageStoreEventPublisher messageStoreEventPublisher, MessageRepositoryRouter messageRepositoryRouter) {
    return new DefaultMessageStoreService(messageStoreEventPublisher, messageRepositoryRouter);
  }

  private static class NoMessageLoader implements MessageLoader {
    @Override
    public <T> List<T> load(String targetTableName, LocalDateTime startTime, LocalDateTime endTime) {
      return Collections.emptyList();
    }

    @Override
    public Lock getScheduleLock(String targetTableName) {
      return new NoLock();
    }
  }

  private static class NoLock implements Lock {
    @Override
    public void lock() {
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
    }

    @Override
    public boolean tryLock() {
      return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
      return false;
    }

    @Override
    public void unlock() {
    }

    @Override
    public Condition newCondition() {
      return null;
    }
  }

  private static class NoMessageStoreEventPublisher implements MessageStoreEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(NoMessageStoreEventPublisher.class);

    @Override
    public void publishEvent(MessageStoreEvent<?> event) {
      log.warn("publish nothing as config without scheduler");
    }
  }
}
