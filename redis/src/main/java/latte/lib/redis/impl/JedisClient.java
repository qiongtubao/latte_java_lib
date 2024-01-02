package latte.lib.redis.impl;

import latte.lib.redis.RedisClient;
import latte.lib.redis.RedisType;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.commands.ProtocolCommand;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class JedisClient extends AbstractRedisClient {
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
    public Object execCommand(String... args) {
        return exec(jedis-> {
            ProtocolCommand command = new ProtocolCommand() {
                @Override
                public byte[] getRaw() {
                    return args[0].getBytes();
                }
            };
            String[] newCommands = new String[args.length - 1];
            System.arraycopy(args, 1, newCommands, 0, newCommands.length);
            return jedis.sendCommand(command, newCommands);
        });
    }

    @Override
    public String infoString(RedisType t, ActionType type) {
        return exec(jedis-> {
            ProtocolCommand command = new ProtocolCommand() {
                @Override
                public byte[] getRaw() {
                    return new String(t.equals(RedisType.CRDT) ? "crdt.info" : "info").getBytes();
                }
            };
            if (type == null) {
                return new String((byte[])jedis.sendCommand(command));
            } else {
                return new String((byte[])jedis.sendCommand(command, type.name().getBytes()));
            }
        });
    }

}
