package latte.lib.api.kv;

public interface DelCommand {
    boolean del(String key) throws Exception;
}
