package latte.lib.stablize.task.impl.redis;

import latte.lib.redis.RedisClient;
import latte.lib.redis.RedisClientFactory;
import latte.lib.redis.RedisConfig;
import latte.lib.redis.RedisType;
import latte.lib.redis.api.CommonCommand;
import latte.lib.stablize.task.AbstractTimerTask;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class MonitorGcTask extends AbstractTimerTask {
    RedisClient client;
    long lastGCHits = 0;
    long lastOps = 0;
    public MonitorGcTask(String host, int port, ScheduledExecutorService scheduledExecutorService, ExecutorService executorService) {
        super(1, scheduledExecutorService, executorService);
        RedisConfig config = new RedisConfig();
        config.setPoolMaxTotal(2);
        config.setPoolIdle(10);
        config.setReadTimeout(2000);
        client = RedisClientFactory.getSingle().createOrGet(
                RedisClientFactory.ClientType.Jedis,
                host,
                port,
                config);
    }

    @Override
    public void doTest() {
        CommonCommand.ActionTypeValue r = client.info(RedisType.CRDT, CommonCommand.ActionType.stats, "stat_gc_hits");
        long gcHits = client.info(RedisType.CRDT, CommonCommand.ActionType.stats, "stat_gc_hits").getLong();
        if (lastGCHits != 0) {
//            logger.info("gc qps: {}", gcHits - lastGCHits);
            System.out.println("gc qps: " + (gcHits - lastGCHits));
        }
        lastGCHits = gcHits;
        long ops = client.info(RedisType.NORMAL, CommonCommand.ActionType.stats, "instantaneous_ops_per_sec").getLong();
//        logger.info("ops:{}" , ops);
        System.out.println("ops: " + (ops/2));
    }
}
