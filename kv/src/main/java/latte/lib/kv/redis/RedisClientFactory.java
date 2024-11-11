package latte.lib.kv.redis;

import java.util.concurrent.ExecutionException;
import latte.lib.api.kv.KVClient;
import latte.lib.kv.redis.impl.jedis2x.DefaultJedisClient;
import latte.lib.kv.redis.impl.vertx.DefaultVertxRedisClient;


public class RedisClientFactory {
  static public KVClient createClient(RedisClientInfo info) throws Exception {
    return new DefaultJedisClient(info.getHost(), info.getPort(), info.getTimeout()
    , info.getMaxTotal() ,info.getMaxIdle());
  }

  static public KVClient createVertxClient(RedisClientInfo info)
      throws ExecutionException, InterruptedException {
    return new DefaultVertxRedisClient(info);
  }
}
