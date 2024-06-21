package latte.lib.api.kv;

import java.util.List;
import java.util.Map;

public interface StringCommand {
    boolean set(String key, String value) throws Exception;
    String get(String key) throws Exception;

    List<String> mget(List<String> keys) throws Exception;
    boolean mset(Map<String, String> map) throws Exception;
}
