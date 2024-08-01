package latte.lib.stabilize.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTimerTask extends AbstractTask{
    private int qps;

    private Future<?> future;

    public AbstractTimerTask(TaskConfig config, ScheduledExecutorService scheduled) {
        super(config, scheduled);
        this.qps = config.getIntArg("qps");
    }

    public int getQps() {
        return qps;
    }


    public abstract void doTest();

    @Override
    public void doStart() {
        long interval = TimeUnit.SECONDS.toNanos(1) / qps;
        future = scheduled.scheduleAtFixedRate(() -> {
            if (!Thread.currentThread().isInterrupted()) {
                scheduled.execute(() -> {
                    try {
                        doTest();
                    } catch (Throwable t) {
                        logger.error(name(), t);
                    }
                });
            }
        }, 0, interval, TimeUnit.NANOSECONDS);

    }

    @Override
    public void doStop() {
        if (future != null)
            future.cancel(true);
    }

    @Override
    public void doInitialize() throws Exception {
        super.doInitialize();

    }

    @Override
    public boolean isStopped() {
        return super.isStopped() && future.isDone();
    }
}
