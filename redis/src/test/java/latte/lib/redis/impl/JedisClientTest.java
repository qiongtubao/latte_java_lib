package latte.lib.redis.impl;

import latte.lib.redis.RedisClient;
import latte.lib.redis.RedisClientFactory;
import latte.lib.redis.RedisConfig;
import org.junit.Test;

public class JedisClientTest {

    @Test
    public void setexTest() {
        RedisConfig redisConfig = new RedisConfig();
        redisConfig.setReadTimeout(2000);
        redisConfig.setPoolIdle(5);
        redisConfig.setPoolMaxTotal(20);
        RedisClient client = RedisClientFactory.getSingle().createOrGet(RedisClientFactory.ClientType.Jedis, "127.0.0.1", 6379, redisConfig);
        client.setex("hello", 1, "value");
    }
}
