package latte.lib.api.config;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import latte.lib.common.serialization.JsonUtils;


public abstract class AbstractDynamicConfig implements DynamicConfig {
  Map<String, String> cached;

  Map<String, Map<Class, List<BiConsumer>>> listens = new LinkedHashMap<>();
  void setCached(Map<String,String> cached) throws Exception {
    Map<String, String> oldCached = this.cached;
    this.cached = cached;
    for(Entry<String,String> kv : oldCached.entrySet()) {
      String key = kv.getKey();
      String value = kv.getValue();
      String nowVal = cached.get(key);
      if (!value.equals(nowVal)) {
        notify(key, value, nowVal);
      }
    }
    for(Entry<String,String> kv : cached.entrySet()) {
      String key = kv.getKey();
      String oldVal = oldCached.get(key);
      if (oldVal == null) {
        String value = kv.getValue();
        notify(key, null, value);
      }
    }

  }

  <T> void notify(String key, String oldVal, String nowVal) throws Exception {
    Map<Class, List<BiConsumer>> maps = this.listens.get(key);
    for(Entry<Class, List<BiConsumer>> kv: maps.entrySet()) {
      Class<T> glass = kv.getKey();
      T old = JsonUtils.decode(oldVal, glass);
      T now = JsonUtils.decode(nowVal, glass);
      kv.getValue().forEach(event -> {
        event.accept(old, now);
      });
    }
  }


  @Override
  public <T> void addListen(String key, Class<T> glass, BiConsumer<T, T> func) {
    Map<Class, List<BiConsumer>> keyListens = this.listens.get(key);
    if (keyListens == null) {
      keyListens = new LinkedHashMap<>();
      this.listens.put(key, keyListens);
    }
    List<BiConsumer> list = keyListens.get(glass);
     if (list == null) {
       list = new LinkedList<>();
       keyListens.put(glass, list);
     }
     list.add(func);
  }


  @Override
  public String getString(String key) {
    return get(key, null);
  }

  @Override
  public String getString(String key, String defaultVal) {
    return this.cached.getOrDefault(key, defaultVal);
  }

  @Override
  public <T> T get(String key, Class<T> glass) {
    return get(key, glass,null);
  }

  @Override
  public <T> T get(String key, Class<T> glass, T val) {
    String value = this.cached.get(key);
    if (value == null) return val;
    try {
      T result = JsonUtils.decode(value, glass);
      return result;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T extends DynamicConfigClass> T getDynmicClass(String key, Class<T> glass) {
    T value = this.get(key, glass);
    this.addListen(key, glass, (old, now) -> {
      try {
        value.change(now);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    return value;
  }
}
