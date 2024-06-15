package latte.lib.stablize.task;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class TaskType {
    static Map<String, Class<Task>> registers = new LinkedHashMap<>();

    static void regionTask(String name, Class<Task> glass) {
        registers.put(name, glass);
    }

    public static Task createTask(String name, TaskContext context, ScheduledThreadPoolExecutor scheduled, ExecutorService executors) throws Exception {
        Class<?>[] glassTypes = new Class[3];
        Object[] glassParams = new Object[3];
        glassTypes[0] = TaskContext.class;
        glassTypes[1] = ScheduledThreadPoolExecutor.class;
        glassTypes[2] = ExecutorService.class;
        glassParams[0] = context;
        glassParams[1] = scheduled;
        glassParams[2] = executors;
        Constructor<? extends Task> constructor = registers.get(name).getConstructor(glassTypes);
        return constructor.newInstance(glassParams);
    }
}
