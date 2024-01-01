package latte.lib.redis.impl;

import latte.lib.redis.RedisClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.function.Function;

public class JedisClient implements RedisClient {
    JedisPool jedisPool;
    String host;
    int port;
    public JedisClient(String host, int port, int timeout, int maxTotal, int maxIdle) {
        this.host = host;
        this.port = port;
        setJedisPool(timeout, maxTotal, maxIdle);
    }

    public void setJedisPool(int timeout, int maxTotal, int maxIdle) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal); // 设置最大连接数
        poolConfig.setMaxIdle(maxIdle); // 设置最大空闲连接数
        jedisPool = new JedisPool(poolConfig, host, port, timeout);
    }

    protected <T> T exec(Function<Jedis, T> predicate) {
        try (Jedis jedis = jedisPool.getResource()) {
            // 执行Redis操作，设置键值对
            return predicate.apply(jedis);
        }
    }
    @Override
    public void setex(String key, long expire, String value) {
        exec(jedis-> {
            jedis.setex(key, expire, value);
            return true;
        });
    }
}
