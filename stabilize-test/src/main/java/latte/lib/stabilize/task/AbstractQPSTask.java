package latte.lib.stabilize.task;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public abstract class AbstractQPSTask extends AbstractTask {
    public static Random random = new Random();

    public AbstractQPSTask(TaskConfig config, ScheduledThreadPoolExecutor scheduled,
        ExecutorService executors) {
        super(config, scheduled, executors);
        this.thread = config.getIntArg("thread");
        this.qps = config.getDoubleArg("qps");
        this.scheduledExecutorService = scheduled;
        this.executorService = executors;

        queue = new ArrayBlockingQueue<>(config.getIntArg("qps"));
    }


    private double qps;

    protected ScheduledExecutorService scheduledExecutorService;

    protected ExecutorService executorService;



    protected int thread;


    BlockingQueue<Runnable> queue;

    public double getQps() {
        return qps;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }


    public abstract Runnable doTest();

    @Override
    public void doStart() {
        executorService.execute(() -> {
            long startTime = System.nanoTime();
            while (!this.isStopped()) {
                for(int i = 0; i < qps; i++) {
                    queue.offer(doTest());
                }
                long sleep =  1000*1000*1000 +  startTime - System.nanoTime();
                if (sleep > 0) {
                    try {
                        if (sleep > 1000000) {
                            Thread.sleep(sleep / 1000000);
                        } else {
                            Thread.sleep(0, (int)sleep);
                        }
                    } catch (Exception e) {
                        logger.error("sleep {} ns fail",sleep, e);
                    }
                } else {
                    logger.error("{} main create task use time {}", this.getClass().getName(), 1000 * 1000 * 1000 - sleep);
                }
                startTime = startTime + 1000*1000*1000;
            }
        });
        for(int j = 0; j < thread; j++) {
            executorService.execute(() -> {
                while(!this.isStopped()) {
                    try {
                        Runnable take = queue.take();
                        take.run();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        logger.info("{} start ok!", this.getClass().getSimpleName());

    }



}
