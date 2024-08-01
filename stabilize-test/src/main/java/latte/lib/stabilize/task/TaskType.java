package latte.lib.stabilize.task;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class TaskType {
    static Map<String, Class<? extends Task>> registers = new LinkedHashMap<>();

    static void regionTask(String name, Class<? extends Task> glass) {
        registers.put(name, glass);
    }

    public static Task createTask(String name, TaskConfig config, ScheduledExecutorService scheduled) throws Exception {
        Class<?>[] glassTypes = new Class[2];
        Object[] glassParams = new Object[2];
        glassTypes[0] = TaskConfig.class;
        glassTypes[1] = ScheduledExecutorService.class;
        glassParams[0] = config;
        glassParams[1] = scheduled;
        Constructor<? extends Task> constructor = registers.get(name).getConstructor(glassTypes);
        return constructor.newInstance(glassParams);
    }
}
