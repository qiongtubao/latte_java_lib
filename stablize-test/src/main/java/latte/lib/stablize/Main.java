package latte.lib.stablize;

import latte.lib.common.utils.JsonUtils;
import latte.lib.stablize.task.TaskConfig;
import latte.lib.stablize.task.TaskManager;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    @Setter
    @Getter
    static public class StabilityConfig {
        Map<String, List<TaskConfig>> tasks;
        int executorNum = 100;
        int scheduledNum = 100;
        public StabilityConfig() {

        }
    }
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("need config file !");
            return;
        }
        try {
            StabilityConfig config = JsonUtils.decodeJsonFile(args[0], StabilityConfig.class);
            TaskManager.createAndRunTasks(config.getTasks(),
                    new ScheduledThreadPoolExecutor(config.getScheduledNum()),
                    Executors.newFixedThreadPool(config.getExecutorNum()));
            Lock lock = new ReentrantLock();
            synchronized (lock) {
                try {
                    // 无限等待，直到其他线程调用lock.notify()或lock.notifyAll()方法
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
