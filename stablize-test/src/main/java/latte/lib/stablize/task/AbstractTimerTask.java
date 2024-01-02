package latte.lib.stablize.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTimerTask extends AbstractTask{
    private int qps;

    protected ScheduledExecutorService scheduledExecutorService;

    protected ExecutorService executorService;

    private Future<?> future;





    public AbstractTimerTask( int qps, ScheduledExecutorService scheduledExecutorService, ExecutorService executorService) {
        this.qps = qps;
        this.scheduledExecutorService = scheduledExecutorService;
        this.executorService = executorService;
    }

    public int getQps() {
        return qps;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public abstract void doTest();

    @Override
    public void doStart() {
        long interval = TimeUnit.SECONDS.toNanos(1) / qps;
        future = scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!Thread.currentThread().isInterrupted()) {
                executorService.execute(() -> {
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
    public void doInitialize() {
        super.doInitialize();

    }

    @Override
    public boolean isStopped() {
        return super.isStopped() && future.isDone();
    }
}
