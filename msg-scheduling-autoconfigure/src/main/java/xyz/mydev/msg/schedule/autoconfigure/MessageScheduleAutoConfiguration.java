package xyz.mydev.msg.schedule.autoconfigure;

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
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import xyz.mydev.msg.common.util.PrefixNameThreadFactory;
import xyz.mydev.msg.schedule.CheckpointScheduler;
import xyz.mydev.msg.schedule.DefaultCheckpointScheduler;
import xyz.mydev.msg.schedule.DefaultMainScheduler;
import xyz.mydev.msg.schedule.DefaultMessageStoreEventListener;
import xyz.mydev.msg.schedule.MainScheduler;
import xyz.mydev.msg.schedule.MessageStoreEventPublisher;
import xyz.mydev.msg.schedule.MessageStoreService;
import xyz.mydev.msg.schedule.ScheduledTableRegistry;
import xyz.mydev.msg.schedule.TableScheduleProperties;
import xyz.mydev.msg.schedule.autoconfigure.properties.SchedulerProperties;
import xyz.mydev.msg.schedule.bean.DelayMessage;
import xyz.mydev.msg.schedule.bean.InstantMessage;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.bridge.event.DefaultMessageStoreEventPublisher;
import xyz.mydev.msg.schedule.bridge.event.GenericMessageStoreEventListenerAdapter;
import xyz.mydev.msg.schedule.bridge.store.DefaultMessageStoreService;
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

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * ???????????????????????????????????????????????????
 * <p>
 * TODO ?????????RocketMqAutoConfiguration???????????????????????????????????????????????????????????????
 * TODO ??????bridge????????????????????????????????????
 *
 * @author ZSP
 */
@Configuration
@AutoConfigureAfter({MessageRepositoryConfiguration.class, RocketMqAutoConfiguration.class})
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


  /**
   * TODO bean??????????????????????????????
   */
  @Bean
  @ConditionalOnMissingBean
  public CheckpointServiceRouter checkpointServiceRouter() {
    DefaultCheckpointServiceRouter router = new DefaultCheckpointServiceRouter();
    // put user custom cpService
    checkpointServiceObjectProvider.forEach(cp -> {
      if (CollectionUtils.isEmpty(cp.getTableNames())) {
        throw new IllegalArgumentException("user custom cp tableNames must not be empty");
      }
      router.put(cp);
    });

    Set<String> tableFromPorter = new HashSet<>();
    porterObjectProvider.forEach(porter ->
      tableFromPorter.add(porter.getTargetTableName()));

    // put default for remaining 1.?????? 2.porter
    Set<String> all = schedulerProperties.scheduledTableNames();
    all.addAll(tableFromPorter);
    all.removeAll(router.tableNameSet());

    if (!all.isEmpty()) {
      RedisCheckPointServiceImpl redisCheckPointService = new RedisCheckPointServiceImpl(redissonClient, messageRepositoryRouter);
      redisCheckPointService.getTableNames().addAll(all);
      // check
      redisCheckPointService.init();
      router.put(redisCheckPointService);
    }
    return router;
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageLoader messageLoader() {
    return new DefaultStringMessageLoader(messageRepositoryRouter, Objects.requireNonNull(redissonClient::getLock));
  }

  /**
   * TODO bean??????????????????????????????
   */
  @Bean
  @ConditionalOnMissingBean
  public PorterRouter messagePorterRouter() {
    DefaultMessagePorterRouter router = new DefaultMessagePorterRouter();
    // put user custom
    porterObjectProvider.forEach(porter -> {
      TableScheduleProperties properties = porter.getTableScheduleProperties();
      if (properties == null) {
        throw new IllegalStateException("customized porter's tableScheduleProperties must be not null");
      }
      porter.init();
      router.put(porter.getTargetTableName(), porter);
      ScheduledTableRegistry.registerTableByBean(porter.getTargetTableName(), properties);
    });

    registerPorterByYml(router);
    log.info("DefaultMessagePorterRouter init result: {}", router);
    return router;
  }


  /**
   * TODO ??????????????????????????????
   */
  @Bean(initMethod = "start", destroyMethod = "stop")
  public CheckpointScheduler checkpointScheduler(CheckpointServiceRouter checkpointServiceRouter) {
    Assert.isTrue(checkpointServiceRouter.size() != 0, "checkpointServiceRouter need init ");
    return new DefaultCheckpointScheduler(checkpointServiceRouter);
  }

  /**
   * TODO ??????????????????????????????
   */
  @Bean(initMethod = "start", destroyMethod = "stop")
  public MainScheduler mainScheduler(PorterRouter porterRouter, MessageLoader messageLoader, CheckpointServiceRouter checkpointServices) {
    DefaultMainScheduler defaultMainScheduler = new DefaultMainScheduler(porterRouter, messageLoader, checkpointServices);
    Assert.isTrue(porterRouter.size() != 0, "porterRouter need init ");
    Assert.isTrue(checkpointServices.size() != 0, "checkpointServices need init ");
    defaultMainScheduler.setScheduledExecutorService(Executors.newScheduledThreadPool(porterRouter.size() * 2, new PrefixNameThreadFactory("MainScheduler")));
    return defaultMainScheduler;
  }


  @Bean
  @ConditionalOnMissingBean
  public DefaultMessageStoreEventListener<StringMessage> messageStoreEventListener(PorterRouter porterRouter) {
    return new DefaultMessageStoreEventListener<>(porterRouter);
  }

  @Bean(initMethod = "init")
  @ConditionalOnBean(DefaultMessageStoreEventListener.class)
  public GenericMessageStoreEventListenerAdapter genericMessageStoreEventListenerAdapter(DefaultMessageStoreEventListener<StringMessage> defaultMessageStoreEventListener) {
    return new GenericMessageStoreEventListenerAdapter(defaultMessageStoreEventListener);
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageStoreEventPublisher messageStoreEventPublisher() {
    return new DefaultMessageStoreEventPublisher();
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageStoreService messageStoreService(MessageStoreEventPublisher messageStoreEventPublisher, MessageRepositoryRouter messageRepositoryRouter) {
    return new DefaultMessageStoreService(messageStoreEventPublisher, messageRepositoryRouter);
  }


  private void registerPorterByYml(DefaultMessagePorterRouter router) {
    registerYmlDelayPorter(router);
    registerYmlInstantPorter(router);
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
