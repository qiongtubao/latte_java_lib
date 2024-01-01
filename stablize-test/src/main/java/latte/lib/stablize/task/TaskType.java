package latte.lib.stablize.task;

import latte.lib.stablize.task.impl.redis.SetexTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public enum TaskType {
    RedisSetex("redisSetex") {
        @Override
        Task createTask(TaskConfig taskConfig, ScheduledExecutorService scheduledExecutorService, ExecutorService executorService) throws Exception {
            return new SetexTask(
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
