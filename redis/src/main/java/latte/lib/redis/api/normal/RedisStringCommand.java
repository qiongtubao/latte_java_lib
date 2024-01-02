package latte.lib.redis.api.normal;

import latte.lib.redis.api.RedisCommand;
import latte.lib.redis.api.StringCommand;

public interface RedisStringCommand extends StringCommand, RedisCommand {
    default void setex(String key, long expire, String value) {
        execCommand("setex",key, String.valueOf(expire), value);
    }

    default void set(String key, String value) {
        execCommand("set",key, value);
    }

    default String get(String key) {
        return parseString(execCommand("get", key));
    }
}
