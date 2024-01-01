package latte.lib.redis;

import latte.lib.redis.impl.JedisClient;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class RedisClientFactory {
    public static enum ClientType {
        Jedis {
            @Override
            RedisClient createClient(String host, int port, RedisConfig config) {
                return new JedisClient(host, port, config.getReadTimeout(), config.getPoolMaxTotal(), config.getPoolIdle());
            }
        };
        abstract RedisClient createClient(String host, int port, RedisConfig config);

    }

    class RedisClientInfo {
        ClientType type;
        String host;
        int port;
        public RedisClientInfo(ClientType type, String host, int port) {
            this.type = type;
            this.host = host;
            this.port = port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RedisClientInfo that = (RedisClientInfo) o;
            return port == that.port && type == that.type && Objects.equals(host, that.host);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, host, port);
        }
    }
    Map<RedisClientInfo, RedisClient> manager = new LinkedHashMap<>();

     public RedisClient createOrGet(ClientType type, String host, int port, RedisConfig config) {
        RedisClientInfo info = new RedisClientInfo(type, host, port);
        RedisClient client = manager.get(info);
        if (client != null) return client;
        synchronized (manager) {
            client = manager.get(info);
            if (client != null) return client;
            client = type.createClient(host, port, config);
            manager.put(info, client);
            return client;
        }
    }
     static RedisClientFactory single = new RedisClientFactory();
     public static RedisClientFactory getSingle() {
         return single;
     }
}
