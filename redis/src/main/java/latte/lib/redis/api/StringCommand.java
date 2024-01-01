package latte.lib.redis.api;

public interface StringCommand {
    void setex(String key, long expire, String value);
}
