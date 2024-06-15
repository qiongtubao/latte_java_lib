package latte.lib.stablize.task;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskContext {

  private String taskName;

  private Map<String, String> args = new LinkedHashMap<>();

  private String clusterName;

  private ScheduledExecutorService taskScheduled;

  private ExecutorService taskExecutors;

  public TaskContext(String clusterName, String taskName, ScheduledExecutorService taskScheduled, ExecutorService taskExecutors, Map<String, String> args) {
    this.clusterName = clusterName;
    this.taskName = taskName;
    this.taskScheduled = taskScheduled;
    this.taskExecutors = taskExecutors;
    this.args = args;
  }
}
