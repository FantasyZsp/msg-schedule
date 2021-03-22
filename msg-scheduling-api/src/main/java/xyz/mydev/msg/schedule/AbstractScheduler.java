package xyz.mydev.msg.schedule;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.load.MessageLoader;
import xyz.mydev.msg.schedule.port.route.PortRouter;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * 未每个需要调度的表构造对应的调度任务，提交到线程池执行
 *
 * @author ZSP
 */
@Slf4j
public abstract class AbstractScheduler<T extends StringMessage> {
  private final Collection<String> scheduledTables;
  private final ScheduleTimeEvaluator scheduleTimeEvaluator;
  private final PortRouter<T> portRouter;
  private final MessageLoader<T> messageLoader;

  private final ScheduledExecutorService scheduledExecutorService;

  public AbstractScheduler(Collection<String> scheduledTables,
                           ScheduleTimeEvaluator scheduleTimeEvaluator,
                           PortRouter<T> portRouter, MessageLoader<T> messageLoader) {
    this.scheduledTables = scheduledTables;
    this.scheduleTimeEvaluator = scheduleTimeEvaluator;
    this.portRouter = portRouter;
    this.messageLoader = messageLoader;

    // TODO 个性化配置线程池
    this.scheduledExecutorService = Executors.newScheduledThreadPool(scheduledTables.size(), r -> new Thread(r, "cpUpdateThread"));
  }


  public void start() {
    // TODO 调整为外部化配置提供需要调度的列表
    for (String tableName : scheduledTables) {
//      Runnable scheduleTask = new ScheduleTask(tableName, portRouter.get(tableName), messageLoader, che);
      //  todo 调度任务
//      CheckpointUpdateStrategy updateStrategy = checkpointService.getUpdateStrategy(tableName);
//      if (updateStrategy != null) {
//        scheduledExecutorService.scheduleWithFixedDelay(() -> updateStrategy.updateCheckpoint(tableName), 2, 30, TimeUnit.MINUTES);
//      }
    }

  }


}
