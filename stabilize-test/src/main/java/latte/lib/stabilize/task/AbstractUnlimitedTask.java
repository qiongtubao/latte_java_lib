package latte.lib.stabilize.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public abstract class AbstractUnlimitedTask extends AbstractTask {
    protected ScheduledExecutorService scheduledExecutorService;

    protected ExecutorService executorService;

    int threadnum;
    public AbstractUnlimitedTask(TaskConfig config, ScheduledThreadPoolExecutor scheduledExecutorService) {
        super(config, scheduledExecutorService);
        this.threadnum = config.getIntArg("threadnum");
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
