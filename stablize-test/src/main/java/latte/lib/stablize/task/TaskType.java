package latte.lib.stablize.task;

import latte.lib.stablize.task.impl.redis.CreateTombstoneTask;
import latte.lib.stablize.task.impl.redis.MonitorGcTask;
import latte.lib.stablize.task.impl.tikv.OneKeyConflictTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public enum TaskType {
    OneKeyConflict("tikvOneKeyConflict") {
        @Override
        Task createTask(TaskConfig taskConfig, ScheduledExecutorService scheduledExecutorService, ExecutorService executorService) throws Exception {
            return new OneKeyConflictTask(
                    taskConfig.getConfig("threads", Integer.class),
                    taskConfig.getConfig("host", String.class),
                    taskConfig.getConfig("port", int.class),
                    taskConfig.getConfig("conflictCount", Integer.class),
                    scheduledExecutorService, executorService);
        }
    },
    MonitorGc("monitorGc") {
        @Override
        Task createTask(TaskConfig taskConfig, ScheduledExecutorService scheduledExecutorService, ExecutorService executorService) throws Exception {
            return new MonitorGcTask(taskConfig.getConfig("host", String.class),
                    taskConfig.getConfig("port", int.class),
                    scheduledExecutorService, executorService);
        }
    },
    CreateTombstone("createTombstone") {
        @Override
        Task createTask(TaskConfig taskConfig, ScheduledExecutorService scheduledExecutorService, ExecutorService executorService) throws Exception {
            return new CreateTombstoneTask(
                    taskConfig.getConfig("host", String.class),
                    taskConfig.getConfig("port", int.class),
                    taskConfig.getConfig("threads", Integer.class),
                    scheduledExecutorService, executorService);
        }
    };
    private String type;
    TaskType(String type) {this.type = type;}

    public String getType() {
        return type;
    }

    public static TaskType form(String type) {

        for (TaskType taskType : values()) {
            if (taskType.getType().equalsIgnoreCase(type)) {
                return taskType;
            }
        }

        throw new IllegalArgumentException("task type: " + type + " not exists!");
    }
    abstract Task createTask(TaskConfig taskConfig, ScheduledExecutorService scheduledExecutorService, ExecutorService executorService) throws Exception;
}
