package latte.lib.api.kv;

import java.util.List;
import java.util.Map;

public interface StringCommand {
    boolean set(String key, String value);
    String get(String key);

    List<String> mget(List<String> keys);
    boolean mset(Map<String, String> map);
}
