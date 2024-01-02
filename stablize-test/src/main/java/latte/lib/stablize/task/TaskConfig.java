package latte.lib.stablize.task;

import latte.lib.common.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Setter
@Getter
public class TaskConfig {
    String name;
    Map<String,String> configs;

    public TaskConfig() {

    }

    public <T>  T getConfig(String key, Class<T> t) throws Exception {
        String v = configs.get(key);
        if (v == null) return null;
        if (t.equals(String.class)) return (T)v;
        return JsonUtils.decode(v, t);
    }
}
