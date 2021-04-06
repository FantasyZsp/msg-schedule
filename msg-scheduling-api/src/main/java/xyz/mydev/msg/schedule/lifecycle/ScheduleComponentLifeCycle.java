package xyz.mydev.msg.schedule.lifecycle;

/**
 * @author ZSP
 */
public interface ScheduleComponentLifeCycle {

  default void start() throws Exception {
  }

  default void stop() {
  }

  default int getOrder() {
    return 0;
  }

}