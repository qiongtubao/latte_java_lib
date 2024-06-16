package latte.lib.stablize.task;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractQPSTask extends AbstractTask {
    public static Random random = new Random();

    public AbstractQPSTask(TaskConfig config, ScheduledThreadPoolExecutor scheduled,
        ExecutorService executors) {
        super(config, scheduled, executors);
        this.clusterName = config.getArg("cluster");
        this.thread = config.getIntArg("thread");
        this.qps = config.getDoubleArg("qps");
        this.scheduledExecutorService = scheduled;
        this.executorService = executors;

//    qpsController = new QpsController(qps, qps * 2, qps, thread);
        queue = new ArrayBlockingQueue<>(config.getIntArg("qps"));
    }

    class TokenBucket {
        private int capacity; // 令牌桶容量
        private int tokens; // 当前令牌数
        private int rate; // 令牌生成速率
        private long lastTime; // 上次生成令牌的时间

        public TokenBucket(int capacity, int rate) {
            this.capacity = capacity;
            this.tokens = capacity;
            this.rate = rate;
            this.lastTime = System.nanoTime();
        }

        public synchronized boolean getToken() {
            long now = System.nanoTime();
            long add = (now - lastTime) * rate / 1000000;
            if (add > 0) {
                tokens += add ; // 根据时间差计算当前令牌数
                if (tokens > capacity) {
                    tokens = capacity;
                }
                lastTime = now;
            } else if (tokens == capacity) {
                lastTime = now;
            }

            if (tokens > 0) {
                tokens--;
                return true;
            } else {
                return false;
            }
        }
    }

    public class QpsController {
        private int qps;
        private TokenBucket tokenBucket;
        private ThreadPoolExecutor executor;

        public QpsController(int qps, int capacity, int rate, int threadCount) {
            this.qps = qps;
            this.tokenBucket = new TokenBucket(capacity, rate);
            this.executor = new ThreadPoolExecutor(threadCount, threadCount, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        }

        public void submitTask(Runnable task) {
            executor.submit(() -> {
                while (true) {
                    if (tokenBucket.getToken()) {
                        task.run();
                    } else {
                        if (qps > 500) {
                            try {
                                Thread.sleep(0, 500000 / qps);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                Thread.sleep(500 /qps );
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }

        public void shutdown() {
            executor.shutdown();
        }
    }
    private double qps;

    protected ScheduledExecutorService scheduledExecutorService;

    protected ExecutorService executorService;

    private Future<?> future;

    protected String clusterName;

//    protected TransactionalKVClient kvClient;

    protected int thread;

    //  private QpsController qpsController;
    BlockingQueue<Runnable> queue;
//    public AbstractQpsTask2(String clusterName, int thread, int qps, ScheduledExecutorService scheduledExecutorService, ExecutorService executorService) {
//
//    }

    public double getQps() {
        return qps;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public String getClusterName() {
        return clusterName;
    }

    public abstract Runnable doTest();

    @Override
    public void doStart() {
        executorService.execute(() -> {
            long startTime = System.nanoTime();
            while (true) {
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
                while(true) {
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

    @Override
    public void doStop() {
        if (future != null)
            future.cancel(true);
    }

    @Override
    public void doInitialize() {
        super.doInitialize();
//        kvClient = null;
//        while(kvClient == null) {
//            Transaction t = Cat.newTransaction("tikv-statlize-test", "getTxnClient");
//            try {
//                kvClient = KVClientFactory.getTxnClient(clusterName);
//                break;
//            } catch (Exception e) {
//                logger.error("getTxnClient",e);
//                Cat.logError(e);
//            } finally {
//                t.complete();
//            }
//            try {
//                Thread.sleep(1000);
//            }catch (Exception e) {
//
//            }
//        }
        logger.info("{} initialize ok!", this.getClass().getSimpleName());
    }

    @Override
    public boolean isStopped() {
        return super.isStopped() && future.isDone();
    }

}
