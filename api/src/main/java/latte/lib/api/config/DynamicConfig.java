package latte.lib.api.config;

import java.util.function.BiConsumer;


public interface DynamicConfig extends DynamicChange {

    <T extends DynamicConfigClass> T getDynmicClass(String key, Class<T> glass) throws Exception;
    String getString(String key, String defaultVal) throws Exception;
    String getString(String key) throws Exception;
    <T> T get(String key, Class<T> glass, T defaultVal) throws Exception;
    <T> T get(String key, Class<T> glass) throws Exception;

    <T> void addListen(String key, Class<T> glass, BiConsumer<T, T> func);
}
