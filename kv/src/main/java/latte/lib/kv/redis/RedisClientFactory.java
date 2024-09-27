package latte.lib.kv.redis;

import latte.lib.api.kv.KVClient;
import latte.lib.kv.redis.impl.DefaultJedisClient;


public class RedisClientFactory {
  static public KVClient createClient(RedisClientInfo info) throws Exception {
    return new DefaultJedisClient(info.getHost(), info.getPort(), info.getTimeout()
    , info.getMaxTotal() ,info.getMaxIdle());
  }
}
