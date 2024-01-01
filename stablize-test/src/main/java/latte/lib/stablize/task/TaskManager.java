package latte.lib.stablize.task;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class TaskManager {
    /* Map<Cluster, Tasks>*/
    Map<String, List<Task>> tasks;

    private ScheduledExecutorService scheduled;

    private ExecutorService executors;
    public TaskManager(Map<String, List<Task>> tasks, ScheduledExecutorService scheduled, ExecutorService executors) {
        this.tasks = tasks;
        this.scheduled = scheduled;
        this.executors = executors;
    }

    public static TaskManager createAndRunTasks(Map<String, List<TaskConfig>> configs, ScheduledExecutorService scheduled, ExecutorService executors) {
        Map<String, List<Task>> tasks = new LinkedHashMap<>();
        configs.forEach((namespace, confs) -> {
            tasks.put(namespace, confs.stream().map(conf-> {
                Task task = null;
                try {
                    task = TaskType.form(conf.getName()).createTask(conf, scheduled, executors);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                task.initialize();
                task.start();
                return task;
            }).collect(Collectors.toList()));
        });
        TaskManager manager = new TaskManager(tasks, scheduled, executors);
        return manager;
    }
}
