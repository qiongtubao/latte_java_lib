package latte.lib.json;

import java.util.Map;

public interface Json {
    public <T> String encode(T t) throws Exception;
    public <T> T decode(String str, Class<T> glass) throws Exception;
        
}
