package latte.lib.stablize.task.impl.redis;

import latte.lib.common.utils.RandomUtils;
import latte.lib.redis.RedisClient;
import latte.lib.redis.RedisClientFactory;
import latte.lib.redis.RedisConfig;
import latte.lib.stablize.task.AbstractUnlimitedTask;
import latte.lib.stablize.task.TaskConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class CreateTombstoneTask extends AbstractUnlimitedTask {
    RedisClient client;
    public CreateTombstoneTask(String host, int port, int threadnum, ScheduledExecutorService scheduledExecutorService, ExecutorService executorService)  {
        super(threadnum, scheduledExecutorService, executorService);
        RedisConfig config = new RedisConfig();
        config.setPoolMaxTotal(threadnum);
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
        String key = RandomUtils.randomString(5);
        client.set(key, "v");
        client.del(key);
    }
}
