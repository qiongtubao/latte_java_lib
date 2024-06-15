package latte.lib.api.db.redis;

public interface HashCommand {
    void hset(String key, String field, String value);

    String hget(String key, String field);
}
