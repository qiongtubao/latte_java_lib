package latte.lib.redis.api.normal;

import latte.lib.redis.api.HashCommand;
import latte.lib.redis.api.RedisCommand;

public interface RedisHashCommand extends RedisCommand, HashCommand {
    @Override
    default void hset(String key, String filed, String value) {
        execCommand("hset", key, filed, value);
    }

    @Override
    default String hget(String key, String field) {
        return parseString(execCommand("hget",key, field));
    }

    @Override
    default void hdel(String key, String field) {
         execCommand("hdel", key, field);
    }
}
