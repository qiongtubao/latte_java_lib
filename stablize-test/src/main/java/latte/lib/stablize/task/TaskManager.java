package latte.lib.stablize.task;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import latte.lib.stablize.StablizeTestConfig;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskManager {
    Map<String, Map<String, Task>> tasks = new LinkedHashMap<>();

    ScheduledThreadPoolExecutor scheduled;
    ExecutorService executors;
    public TaskManager(StablizeTestConfig config) {
        scheduled = new ScheduledThreadPoolExecutor(config.getScheduledNum());
        executors =  Executors.newFixedThreadPool(config.getExecutorNum());
        config.getTasks().forEach((clusterName, confs) -> {
            Map<String, Task> ts = new LinkedHashMap<>();
            confs.forEach((taskName, conf) -> {
                ts.put(conf.getTaskName(), createTask(clusterName, conf));
            });
            this.tasks.put(clusterName, ts);
        });
    }

    public Task createTask(String clusterName, TaskConfig conf) {
        TaskContext taskContext = new TaskContext(clusterName, conf.getTaskName(), scheduled, executors, conf.getArgs());
        Task task = null;
        try {
            task = TaskType.createTask(conf.getTaskName(), taskContext, scheduled, executors);
            task.initialize();
            task.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return task;
    }

    public void stop() {
        this.tasks.forEach((key, map) -> {
            map.forEach((taskName, task) -> {
                task.stop();
            });
        });
    }

    public void setScheduledNum(int num) {
        this.scheduled.setCorePoolSize(num);
    }

    public void setExecutorsNum(int num) {
        ((ThreadPoolExecutor)(executors)).setCorePoolSize(num);
    }


    public int updateTasks(Map<String, Map<String, TaskConfig>> config) {
        int result = 0;
        if (config == null) {
            result = this.tasks.size();
            this.stop();
            this.tasks = new LinkedHashMap<>();
            return result;
        }
        for(Entry<String, Map<String, TaskConfig>> kv:config.entrySet()) {
            String clusterName = kv.getKey();
            Map<String, TaskConfig> configs = kv.getValue();
            Map<String, Task> tasks = this.tasks.get(clusterName);
            if (tasks == null) {
                Map<String, Task> ts = new LinkedHashMap<>();
                configs.forEach((taskName, conf) -> {
                    ts.put(taskName, createTask(clusterName, conf));
                });
                this.tasks.put(clusterName, ts);
                continue;
            }
            if (configs.size() == 0) {
                tasks.forEach((taskName, task) -> {
                    task.stop();
                });
                this.tasks.remove(clusterName);
                continue;
            }
            for(Entry<String,TaskConfig> kt: configs.entrySet()) {
                String taskName = kt.getKey();
                Task task = tasks.get(taskName);
                if (task != null) {
                    if (task.getConfig().equals(kt.getValue())) {
                        continue;
                    } else {
                        task.stop();
                    }
                }
                tasks.put(taskName, createTask(clusterName, kt.getValue()));
            }
            for(Entry<String, Task> kt: tasks.entrySet()) {
                String taskName = kt.getKey();
                TaskConfig conf = configs.get(taskName);
                if (conf == null) {
                    kt.getValue().stop();
                    tasks.remove(taskName);
                }
            }

        }

        return result;
    }

    public static TaskManager createTaskManager(StablizeTestConfig config) {
        TaskManager taskManager = new TaskManager(config);
        return taskManager;
    }
}
