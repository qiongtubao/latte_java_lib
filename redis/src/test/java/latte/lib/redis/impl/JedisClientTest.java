package latte.lib.redis.impl;

import latte.lib.redis.RedisClient;
import latte.lib.redis.RedisClientFactory;
import latte.lib.redis.RedisConfig;
import latte.lib.redis.RedisType;
import latte.lib.redis.api.CommonCommand;
import org.junit.Assert;
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
        Assert.assertEquals(client.get("hello"), "value");
        client.set("hello", "value1");
        Assert.assertEquals(client.get("hello"), "value1");
        client.del("hello");
        Assert.assertEquals(client.get("hello"), null);
    }

    @Test
    public void infoTest() {
        RedisConfig redisConfig = new RedisConfig();
        redisConfig.setReadTimeout(2000);
        redisConfig.setPoolIdle(5);
        redisConfig.setPoolMaxTotal(20);
        RedisClient client = RedisClientFactory.getSingle().createOrGet(RedisClientFactory.ClientType.Jedis, "127.0.0.1", 6379, redisConfig);
        CommonCommand.ActionTypeValue value = client.info(RedisType.NORMAL, CommonCommand.ActionType.Keyspace, "db0");

        CommonCommand.ActionTypeValue v = value.getMap().get("keys");
        System.out.println("v   :"+v.getString());
        long keys = v.getLong();
        System.out.println(keys);

    }
}
