package latte.lib.stabilize.task;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class AbstractQpsTask extends AbstractTask {
    public static Random random = new Random();

    AtomicInteger atomic = new AtomicInteger(0);

    public AbstractQpsTask(TaskConfig config, ScheduledThreadPoolExecutor scheduled,
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
    static enum EventType {
        EXEC,
        STOP
    };
    static class RunEvent {
        EventType type;
        Runnable runnable;
        public RunEvent(EventType type, Runnable runnable) {
            this.type = type;
            this.runnable = runnable;
        }
    }
    BlockingQueue<RunEvent> queue;
    @Override
    public void doStart() {
        executorService.execute(() -> {
            logger.info("[thread-{}] {} start  assignment", Thread.currentThread(), getClass().getSimpleName());
            long startTime = System.nanoTime();
            atomic.incrementAndGet();
            while (!this.isStopped()) {
                try {
                    for (int i = 0; i < qps; i++) {
                        queue.offer(new RunEvent(EventType.EXEC, doTest()));
                    }
                    long sleep = 1000 * 1000 * 1000 + startTime - System.nanoTime();
                    if (sleep > 0) {
                        try {
                            if (sleep > 1000000) {
                                Thread.sleep(sleep / 1000000);
                            } else {
                                Thread.sleep(0, (int) sleep);
                            }
                        } catch (Exception e) {
                            logger.error("sleep {} ns fail", sleep, e);
                        }
                    } else {
                        logger.error("{} main create task use time {}", this.getClass().getName(), 1000 * 1000 * 1000 - sleep);
                    }
                    startTime = startTime + 1000 * 1000 * 1000;
                } catch (Exception e) {
                    logger.error("send exec event fail", e);
                }
            }
            int j = 0;
            while (j < thread && atomic.get() > 1) {
                try {
                    if(queue.offer(new RunEvent(EventType.STOP, null))) {
                        j++;
                    } else {
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    logger.error("{} send stop event fail", getClass().getSimpleName(),e);
                }
            }
            atomic.decrementAndGet();
            logger.info("[thread-{}] {} stop  assignment", Thread.currentThread(), getClass().getSimpleName());
        });
        for(int j = 0; j < thread; j++) {
            executorService.execute(() -> {
                atomic.incrementAndGet();
                logger.info("[thread-{}] {} start exec task", Thread.currentThread(), getClass().getSimpleName());
                while(!this.isStopped()) {
                    try {
                        RunEvent take = queue.take();
                        if (take.type.equals(EventType.STOP)) {
                            continue;
                        }
                        take.runnable.run();
                    } catch (InterruptedException e) {
                        logger.error("{} task exec fail:", getClass().getSimpleName(),e);
                    }
                }
                atomic.decrementAndGet();
                logger.info("[thread-{}] {} stop exec task", Thread.currentThread(), getClass().getSimpleName());
            });
        }
        super.doStart();

    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        if (this.isStopped()) {
            int trycount = 0;
            while(atomic.get() != 0) {
                if (trycount % 60 == 0) {
                    logger.info("wait {} task stop {}s  wait {} thread", this.getClass().getSimpleName(), trycount, atomic.get());
                }
                trycount++;
                try {
                    Thread.sleep(1000);
                }catch (Exception e) {
                    throw new IllegalStateException(String.format("wait %s task stop ,sleep fail", this.getClass().getSimpleName(), e));
                }
            }
        } else {
            logger.error("{} stop fail", this.getClass().getSimpleName());
        }

    }
}
