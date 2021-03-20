package xyz.mydev.msg.schedule;

import lombok.extern.slf4j.Slf4j;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.load.ScheduleTimeEvaluator;
import xyz.mydev.msg.schedule.load.checkpoint.CheckpointService;
import xyz.mydev.msg.schedule.port.route.PortRouter;


/**
 * 未每个需要调度的表构造对应的调度任务，提交到线程池执行
 *
 * @author ZSP
 */
@Slf4j
public abstract class AbstractScheduler<T extends StringMessage> {
  private final CheckpointService checkpointService;
  private final ScheduleTimeEvaluator scheduleTimeEvaluator;
  private final PortRouter<T> portRouter;

  public AbstractScheduler(CheckpointService checkpointService,
                           ScheduleTimeEvaluator scheduleTimeEvaluator,
                           PortRouter<T> portRouter) {
    this.checkpointService = checkpointService;
    this.scheduleTimeEvaluator = scheduleTimeEvaluator;
    this.portRouter = portRouter;
  }



}
