package latte.lib.redis.api;

public interface HashCommand {
    void hset(String key, String field, String value);

    String hget(String key, String field);

    void hdel(String key, String field);
}
