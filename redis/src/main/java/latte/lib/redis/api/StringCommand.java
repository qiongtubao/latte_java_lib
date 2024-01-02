package latte.lib.redis.api;

public interface StringCommand {
    void setex(String key, long expire, String value);

    void set(String key, String value);

    String get(String key);
}
