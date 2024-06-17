package latte.lib.stabilize.task;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import latte.lib.stabilize.StablizeTestConfig;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Setter
@Getter
public class TaskManager {
    Map<String, Map<String, Task>> tasks = new LinkedHashMap<>();

    ScheduledThreadPoolExecutor scheduled;
    ExecutorService executors;

    Logger logger = LoggerFactory.getLogger(TaskManager.class);
    public TaskManager(StablizeTestConfig config) {
        scheduled = new ScheduledThreadPoolExecutor(config.getScheduledNum());
        executors =  Executors.newFixedThreadPool(config.getExecutorNum());
        config.getTasks().forEach((clusterName, confs) -> {
            Map<String, Task> ts = new LinkedHashMap<>();
            confs.forEach((taskName, conf) -> {
                Task task = null;
                try {
                    task = createTask(clusterName, taskName, conf);
                    ts.put(taskName, task);
                } catch (Exception e) {
                    //一个任务错误不影响其他任务
                    logger.error("create task fail , clusterName: {} taskName: {}, conf: {}, error: {}"
                        , clusterName, taskName, conf, e);
                }
            });
            this.tasks.put(clusterName, ts);
        });
    }

    public Task createTask(String clusterName,String taskName, TaskConfig conf) throws Exception {
        Task task = null;
        task = TaskType.createTask(taskName, conf, scheduled, executors);
        task.initialize();
        task.start();
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
                    Task task = null;
                    try {
                        task = createTask(clusterName, taskName, conf);
                        ts.put(taskName, task);
                    } catch (Exception e) {
                        logger.error("[updateTasks] add task fail, clusterName: {}, taskName: {}, conf: {}, e: {}",
                            clusterName, taskName, conf, e);
                    }

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
                    tasks.remove(taskName);
                }
                try {
                    task = createTask(clusterName, taskName, kt.getValue());
                    tasks.put(taskName, task);
                } catch (Exception e) {
                    logger.error("[updateTasks] updateOrAdd Task fail , clusterName:{} taskName: {} config: {} error: {}",
                        clusterName, taskName, config, e);
                }
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
