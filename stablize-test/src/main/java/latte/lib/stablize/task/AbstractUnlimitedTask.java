package latte.lib.stablize.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public abstract class AbstractUnlimitedTask extends AbstractTask {
    protected ScheduledExecutorService scheduledExecutorService;

    protected ExecutorService executorService;

    int threadnum;
    public AbstractUnlimitedTask(int threadnum, ScheduledExecutorService scheduledExecutorService, ExecutorService executorService) {
        this.threadnum = threadnum;
        this.scheduledExecutorService = scheduledExecutorService;
        this.executorService = executorService;
    }

    public abstract void doTest();

    @Override
    public void doStart() {
        for(int i = 0; i < this.threadnum ;i++) {
            executorService.execute(() -> {
                while (!isStopped()) {
                    try {
                        doTest();
                    } catch (Throwable t) {
                        logger.error(name(), t);
                    }
                }
            });
        }
        logger.info("{} start ok!", this.getClass().getSimpleName());
    }
}
