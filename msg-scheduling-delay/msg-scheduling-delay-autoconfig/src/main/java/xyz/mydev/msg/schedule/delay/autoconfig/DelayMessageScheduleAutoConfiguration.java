package xyz.mydev.msg.schedule.delay.autoconfig;

import com.sishu.redis.lock.annotation.RedisLockAnnotationSupportAutoConfig;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import xyz.mydev.msg.schedule.delay.infrastruction.repository.bean.DelayMessage;
import xyz.mydev.msg.schedule.delay.port.DefaultDelayMessagePorter;
import xyz.mydev.msg.schedule.delay.port.RedisDelayTransferQueue;
import xyz.mydev.msg.schedule.infrastruction.repository.MessageRepository;
import xyz.mydev.msg.schedule.infrastruction.repository.route.DefaultMessageRepositoryRouter;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;
import xyz.mydev.msg.schedule.load.DefaultStringMessageLoader;
import xyz.mydev.msg.schedule.load.MessageLoader;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;
import xyz.mydev.msg.schedule.load.checkpoint.redis.RedisCheckPointServiceImpl;
import xyz.mydev.msg.schedule.load.checkpoint.route.CheckpointServiceRouter;
import xyz.mydev.msg.schedule.load.checkpoint.route.DefaultCheckpointServiceRouter;
import xyz.mydev.msg.schedule.port.DefaultPorter;
import xyz.mydev.msg.schedule.port.Porter;
import xyz.mydev.msg.schedule.port.TransferDirectlyTaskFactory;
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
  private final RedissonClient redissonClient;

  public DelayMessageScheduleAutoConfiguration(SchedulerProperties schedulerProperties,
                                               ObjectProvider<MessageRepository<? extends StringMessage>> provider,
                                               ObjectProvider<CheckpointService> checkpointServiceObjectProvider,
                                               ObjectProvider<Porter<?>> porterObjectProvider,
                                               RedissonClient redissonClient) {
    this.provider = provider;
    this.schedulerProperties = schedulerProperties;
    this.porterObjectProvider = porterObjectProvider;
    this.redissonClient = redissonClient;
    this.checkpointServiceObjectProvider = checkpointServiceObjectProvider;
  }

  private static final Logger log = LoggerFactory.getLogger(DelayMessageScheduleAutoConfiguration.class);

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
      RedisCheckPointServiceImpl redisCheckPointService = new RedisCheckPointServiceImpl(redissonClient, messageRepositoryRouter());
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
    return new DefaultStringMessageLoader(messageRepositoryRouter(), Objects.requireNonNull(redissonClient::getLock));
  }

  @Bean
  @ConditionalOnMissingBean
  public PorterRouter messagePorterRouter() {
    DefaultMessagePorterRouter router = new DefaultMessagePorterRouter();

    // put user custom
    porterObjectProvider.ifAvailable(porter ->
      router.putAny(TableKeyPair.of(porter.getTargetTableName(), porter.getTableEntityClass()), porter));

    // put yml config for delay
    Map<String, TableScheduleProperties> delayTableNames = schedulerProperties.getRoute().getTables().getDelay();

    delayTableNames.forEach((key, tableScheduleProperty) -> {
      TableKeyPair<?> tableKeyPair = TableKeyPair.of(tableScheduleProperty.getTableName(), tableScheduleProperty.getTableEntityClass());
      // TODO 构建 PortTaskFactory
      RedisDelayTransferQueue<DelayMessage> transferQueue = new RedisDelayTransferQueue<>(redissonClient, tableScheduleProperty.getTableName());
      TransferDirectlyTaskFactory<DelayMessage> transferDirectlyTaskFactory = new TransferDirectlyTaskFactory<>(transferQueue);


      Porter<?> build =
        DefaultDelayMessagePorter.buildDefaultDelayMessagePorter(tableKeyPair.getTableName(),
          (Class<DelayMessage>) tableKeyPair.getTargetClass(),
          transferQueue,
          transferDirectlyTaskFactory,
          null);
      router.putIfAbsent(tableKeyPair, build);
    });

    // put yml config for instant
    Map<String, TableScheduleProperties> instantTableNames = schedulerProperties.getRoute().getTables().getInstant();

    instantTableNames.forEach((key, tableScheduleProperty) -> {
      TableKeyPair<?> tableKeyPair = TableKeyPair.of(tableScheduleProperty.getTableName(), tableScheduleProperty.getTableEntityClass());
      // TODO 构建 transferQueue、TransferTaskFactory、PortTaskFactory
      RedisDelayTransferQueue<DelayMessage> transferQueue = new RedisDelayTransferQueue<>(redissonClient, tableScheduleProperty.getTableName());
      TransferDirectlyTaskFactory<DelayMessage> transferDirectlyTaskFactory = new TransferDirectlyTaskFactory<>(transferQueue);

      Porter<?> build =
        DefaultPorter.build(tableKeyPair.getTableName(),
          (Class<DelayMessage>) tableKeyPair.getTargetClass(),
          transferQueue, transferDirectlyTaskFactory, null);
      router.putIfAbsent(tableKeyPair, build);
    });


    log.info("DefaultMessagePorterRouter init result: {}", router);

    return router;
  }

}
