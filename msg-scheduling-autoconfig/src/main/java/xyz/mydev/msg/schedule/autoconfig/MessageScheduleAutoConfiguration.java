package xyz.mydev.msg.schedule.autoconfig;

import com.sishu.redis.lock.annotation.RedisLockAnnotationSupportAutoConfig;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import xyz.mydev.msg.schedule.ScheduledTableRegistry;
import xyz.mydev.msg.schedule.TableScheduleProperties;
import xyz.mydev.msg.schedule.autoconfig.properties.SchedulerProperties;
import xyz.mydev.msg.schedule.bean.DelayMessage;
import xyz.mydev.msg.schedule.bean.InstantMessage;
import xyz.mydev.msg.schedule.delay.port.RedisDelayTransferQueue;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;
import xyz.mydev.msg.schedule.load.DefaultStringMessageLoader;
import xyz.mydev.msg.schedule.load.MessageLoader;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;
import xyz.mydev.msg.schedule.load.checkpoint.redis.RedisCheckPointServiceImpl;
import xyz.mydev.msg.schedule.load.checkpoint.route.CheckpointServiceRouter;
import xyz.mydev.msg.schedule.load.checkpoint.route.DefaultCheckpointServiceRouter;
import xyz.mydev.msg.schedule.mq.producer.MqProducer;
import xyz.mydev.msg.schedule.mq.rocketmq.autoconfig.RocketMqAutoConfiguration;
import xyz.mydev.msg.schedule.port.DefaultDelayMessagePortTaskFactory;
import xyz.mydev.msg.schedule.port.DefaultDelayMessagePorter;
import xyz.mydev.msg.schedule.port.DefaultDelayMessageTransferTaskFactory;
import xyz.mydev.msg.schedule.port.DefaultInstantMessagePorter;
import xyz.mydev.msg.schedule.port.DefaultInstantMessageTransferTaskFactory;
import xyz.mydev.msg.schedule.port.Porter;
import xyz.mydev.msg.schedule.port.route.DefaultMessagePorterRouter;
import xyz.mydev.msg.schedule.port.route.PorterRouter;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 根据外部化配置，自动装配需要的组件
 * <p>
 * TODO 分离对RocketMqAutoConfiguration的依赖，提供中间层，统一自动装配消息中间件
 *
 * @author ZSP
 */
@Configuration
@AutoConfigureAfter({MessageRepositoryConfiguration.class, RedisLockAnnotationSupportAutoConfig.class, RocketMqAutoConfiguration.class})
@ConditionalOnBean({RedissonClient.class, MqProducer.class})
@ConditionalOnProperty(value = "msg-schedule.scheduler.enable", havingValue = "true")
@EnableConfigurationProperties({SchedulerProperties.class})
public class MessageScheduleAutoConfiguration implements InitializingBean {

  private final MessageRepositoryRouter messageRepositoryRouter;
  private final ObjectProvider<CheckpointService> checkpointServiceObjectProvider;
  private final ObjectProvider<Porter<?>> porterObjectProvider;
  private final SchedulerProperties schedulerProperties;
  private final MqProducer mqProducer;
  private final RedissonClient redissonClient;

  public MessageScheduleAutoConfiguration(SchedulerProperties schedulerProperties,
                                          MessageRepositoryRouter messageRepositoryRouter,
                                          ObjectProvider<CheckpointService> checkpointServiceObjectProvider,
                                          ObjectProvider<Porter<?>> porterObjectProvider,
                                          MqProducer mqProducer,
                                          RedissonClient redissonClient) {
    this.messageRepositoryRouter = messageRepositoryRouter;
    this.schedulerProperties = schedulerProperties;
    this.porterObjectProvider = porterObjectProvider;
    this.mqProducer = mqProducer;
    this.redissonClient = redissonClient;
    this.checkpointServiceObjectProvider = checkpointServiceObjectProvider;
  }

  private static final Logger log = LoggerFactory.getLogger(MessageScheduleAutoConfiguration.class);


  @Bean
  @ConditionalOnMissingBean
  public CheckpointServiceRouter checkpointServiceRouter() {
    DefaultCheckpointServiceRouter router = new DefaultCheckpointServiceRouter();
    // put user custom
    checkpointServiceObjectProvider.ifAvailable(cp -> {

      if (CollectionUtils.isEmpty(cp.getTableNames())) {
        throw new IllegalArgumentException("user custom cp tableNames must not be empty");
      }
      router.put(cp);

    });
    // put default for remaining
    Set<String> all = schedulerProperties.scheduledTableNames();
    all.removeAll(router.tableNameSet());

    if (!all.isEmpty()) {
      RedisCheckPointServiceImpl redisCheckPointService = new RedisCheckPointServiceImpl(redissonClient, messageRepositoryRouter);
      redisCheckPointService.getTableNames().addAll(all);
      redisCheckPointService.init();
      router.put(redisCheckPointService);
    }
    return router;
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnBean({MessageRepositoryRouter.class, RedissonClient.class})
  public MessageLoader messageLoader() {
    return new DefaultStringMessageLoader(messageRepositoryRouter, Objects.requireNonNull(redissonClient::getLock));
  }

  /**
   * TODO bean注册与初始化逻辑分开
   */
  @Bean
  @ConditionalOnMissingBean
  public PorterRouter messagePorterRouter() {
    DefaultMessagePorterRouter router = new DefaultMessagePorterRouter();
    // put user custom
    porterObjectProvider.ifAvailable(porter -> {
      TableScheduleProperties properties = porter.getTableScheduleProperties();
      if (properties == null) {
        throw new IllegalStateException("customized porter's tableScheduleProperties must be not null");
      }
      router.put(porter.getTargetTableName(), porter);
      ScheduledTableRegistry.registerTableByBean(porter.getTargetTableName(), properties);
    });

    registerYmlDelayPorter(router);
    registerYmlInstantPorter(router);
    log.debug("DefaultMessagePorterRouter init result: {}", router);

    return router;
  }

  private void registerYmlInstantPorter(DefaultMessagePorterRouter router) {
    // put yml config for instant
    Map<String, TableScheduleProperties> instantTableNames = schedulerProperties.getRoute().getTables().getInstant();

    instantTableNames.forEach((key, tableScheduleProperty) -> {
      Class<?> tableEntityClass = tableScheduleProperty.getTableEntityClass();
      String tableName = tableScheduleProperty.getTableName();

      if (!InstantMessage.class.isAssignableFrom(tableEntityClass)) {
        throw new IllegalArgumentException("instant msg class must implements InstantMessage");
      }

      DefaultInstantMessagePorter porter = new DefaultInstantMessagePorter(tableName, new DefaultInstantMessageTransferTaskFactory(mqProducer));
      // don't forget to set tableScheduleProperty
      porter.setTableScheduleProperties(tableScheduleProperty);
      porter.init();
      router.putIfAbsent(tableName, porter);
      ScheduledTableRegistry.registerTableByConfig(tableName, tableScheduleProperty);

    });
  }

  private void registerYmlDelayPorter(DefaultMessagePorterRouter router) {
    // put yml config for delay
    Map<String, TableScheduleProperties> delayTableNames = schedulerProperties.getRoute().getTables().getDelay();

    delayTableNames.forEach((key, tableScheduleProperty) -> {

      Class<?> tableEntityClass = tableScheduleProperty.getTableEntityClass();
      String tableName = tableScheduleProperty.getTableName();

      if (!DelayMessage.class.isAssignableFrom(tableEntityClass)) {
        throw new IllegalArgumentException("delay msg class must implements DelayMessage");
      }

      RedisDelayTransferQueue<DelayMessage> transferQueue = new RedisDelayTransferQueue<>(redissonClient, tableName);

      DefaultDelayMessageTransferTaskFactory<DelayMessage> defaultDelayMessageTransferTaskFactory = new DefaultDelayMessageTransferTaskFactory<>(transferQueue);
      Porter<DelayMessage> porter =
        new DefaultDelayMessagePorter(tableName,
          transferQueue,
          defaultDelayMessageTransferTaskFactory,
          new DefaultDelayMessagePortTaskFactory(mqProducer, transferQueue));
      // don't forget to set tableScheduleProperty
      porter.setTableScheduleProperties(tableScheduleProperty);
      porter.init();
      router.putIfAbsent(tableName, porter);
      ScheduledTableRegistry.registerTableByConfig(tableName, tableScheduleProperty);
    });
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    schedulerProperties.init();
  }
}
