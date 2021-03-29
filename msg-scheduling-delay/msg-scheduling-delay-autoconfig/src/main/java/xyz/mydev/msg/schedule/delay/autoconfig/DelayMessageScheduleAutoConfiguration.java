package xyz.mydev.msg.schedule.delay.autoconfig;

import com.sishu.redis.lock.annotation.RedisLockAnnotationSupportAutoConfig;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.mydev.msg.common.TableKeyPair;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.delay.autoconfig.properties.SchedulerProperties;
import xyz.mydev.msg.schedule.delay.autoconfig.properties.TableScheduleProperties;
import xyz.mydev.msg.schedule.delay.port.DefaultDelayMessagePorter;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;
import xyz.mydev.msg.schedule.infrastruction.repository.route.DefaultMessageRepositoryRouter;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;
import xyz.mydev.msg.schedule.load.DefaultStringMessageLoader;
import xyz.mydev.msg.schedule.load.MessageLoader;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;
import xyz.mydev.msg.schedule.load.checkpoint.redis.RedisCheckPointServiceImpl;
import xyz.mydev.msg.schedule.load.checkpoint.route.CheckpointServiceRouter;
import xyz.mydev.msg.schedule.load.checkpoint.route.DefaultCheckpointServiceRouter;
import xyz.mydev.msg.schedule.port.Porter;
import xyz.mydev.msg.schedule.port.route.DefaultMessagePorterRouter;
import xyz.mydev.msg.schedule.port.route.PorterRouter;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 根据外部化配置，自动装配需要的组件
 *
 * @author ZSP
 */
@Configuration
@ConditionalOnBean({RedissonClient.class})
@AutoConfigureAfter({RedisLockAnnotationSupportAutoConfig.class})
@ConditionalOnProperty(value = "msg-schedule.scheduler.enable", havingValue = "true")
@EnableConfigurationProperties({SchedulerProperties.class})
public class DelayMessageScheduleAutoConfiguration {

  private final ObjectProvider<MessageRepository<? extends StringMessage>> provider;
  private final ObjectProvider<CheckpointService> checkpointServiceObjectProvider;
  private final ObjectProvider<Porter<?>> porterObjectProvider;
  private final SchedulerProperties schedulerProperties;
  private final ObjectProvider<RedissonClient> redissonClientObjectProvider;

  public DelayMessageScheduleAutoConfiguration(SchedulerProperties schedulerProperties,
                                               ObjectProvider<MessageRepository<? extends StringMessage>> provider,
                                               ObjectProvider<CheckpointService> checkpointServiceObjectProvider,
                                               ObjectProvider<Porter<?>> porterObjectProvider, ObjectProvider<RedissonClient> redissonClientObjectProvider) {
    this.provider = provider;
    this.schedulerProperties = schedulerProperties;
    this.porterObjectProvider = porterObjectProvider;
    this.redissonClientObjectProvider = redissonClientObjectProvider;
    this.checkpointServiceObjectProvider = checkpointServiceObjectProvider;
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageRepositoryRouter messageRepositoryRouter() {
    DefaultMessageRepositoryRouter router = new DefaultMessageRepositoryRouter();
    provider.ifAvailable(repository -> router.put(repository.getTableName(), repository));
    return router;
  }

  @Bean
  @ConditionalOnMissingBean
  public CheckpointServiceRouter checkpointServiceRouter() {
    DefaultCheckpointServiceRouter router = new DefaultCheckpointServiceRouter();
    // put user custom
    checkpointServiceObjectProvider.ifAvailable(router::put);
    // put default for remaining
    Set<String> all = schedulerProperties.getScheduledTableNames();
    all.removeAll(router.tableNameSet());

    if (!all.isEmpty()) {
      RedisCheckPointServiceImpl redisCheckPointService = new RedisCheckPointServiceImpl(Objects.requireNonNull(redissonClientObjectProvider.getIfAvailable()), messageRepositoryRouter());
      for (String scheduledTables : all) {
        router.putIfAbsent(scheduledTables, redisCheckPointService);
      }
      redisCheckPointService.init();
    }
    return router;
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageLoader messageLoader() {
    return new DefaultStringMessageLoader(messageRepositoryRouter(), Objects.requireNonNull(redissonClientObjectProvider.getIfAvailable())::getLock);
  }


  /**
   * TODO Porter需要重构
   * 支持用户为某个表自定义porter
   */
  @Bean
  @ConditionalOnMissingBean
  public PorterRouter messagePorterRouter() {
    DefaultMessagePorterRouter router = new DefaultMessagePorterRouter();

    Map<String, TableScheduleProperties> delayTableNames = schedulerProperties.getRoute().getTables().getDelay();
    for (Map.Entry<String, TableScheduleProperties> delay : delayTableNames.entrySet()) {
      TableScheduleProperties value = delay.getValue();
      TableKeyPair<?> tableKeyPair = TableKeyPair.of(value.getTableName(), value.getTableEntityClass());
      Porter<?> build = DefaultDelayMessagePorter.build(tableKeyPair.getTargetClass());
      router.putAny(tableKeyPair, build);
    }
    return router;
  }

}