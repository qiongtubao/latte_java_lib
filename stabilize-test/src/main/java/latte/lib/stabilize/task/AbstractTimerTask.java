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


    public abstract Runnable doTest();

    @Override
    public void doStart() {
        long interval = TimeUnit.SECONDS.toNanos(1) / qps;
        future = scheduled.scheduleAtFixedRate(() -> {
            scheduled.submit(doTest());
        }, 0, interval, TimeUnit.NANOSECONDS);
        super.doStart();
    }

    @Override
    public void doStop() {
        if (future != null) {
            while(!future.cancel(true)) {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {

                }
                logger.error("[latte] {} cancel fail", this.getClass().getSimpleName());
            }

            future = null;
            super.doStop();
        }

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
