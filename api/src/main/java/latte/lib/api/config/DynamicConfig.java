package latte.lib.api.config;

import java.util.function.BiConsumer;


public interface DynamicConfig {
    String getString(String key, String defaultVal);
    String getString(String key);
    <T> T get(String key, Class<T> glass, T defaultVal) throws Exception;
    <T> T get(String key, Class<T> glass) throws Exception;

    <T> void addListen(String key, Class<T> glass, BiConsumer<T, T> func);
}
