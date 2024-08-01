package latte.lib.api.config;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import latte.lib.common.serialization.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractDynamicConfig implements DynamicConfig {
  static Logger logger = LoggerFactory.getLogger(AbstractDynamicConfig.class);
  Map<String, Object> cached;

  Map<String, Map<Class, List<BiConsumer>>> listens = new LinkedHashMap<>();
  void setCached(Map<String, Object> cached) throws Exception {
    Map<String, Object> oldCached = this.cached;
    this.cached = cached;
    boolean changed = false;
    for(Entry<String,Object> kv : oldCached.entrySet()) {
      String key = kv.getKey();
      String value = JsonUtils.encode(kv.getValue());
      String nowVal = JsonUtils.encode(cached.get(key));
      if (!value.equals(nowVal)) {
        notify(key, value, nowVal);
        changed = true;
      }
    }
    for(Entry<String,Object> kv : cached.entrySet()) {
      String key = kv.getKey();
      Object oldVal = oldCached.get(key);
      if (oldVal == null) {
        String value = JsonUtils.encode(kv.getValue());
        notify(key, null, value);
        changed = true;
      }
    }
    if (changed) {
        logger.info("[dynamic-config] change {} -> {}",
            JsonUtils.encode(oldCached),
            JsonUtils.encode(cached)
        );
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
  public String getString(String key) throws Exception {
    return getString(key, null);
  }

  @Override
  public String getString(String key, String defaultVal) throws Exception {
    return JsonUtils.encode(this.cached.getOrDefault(key, defaultVal)) ;
  }

  @Override
  public <T> T get(String key, Class<T> glass) throws Exception {
    return get(key, glass,null);
  }

  @Override
  public <T> T get(String key, Class<T> glass, T val) throws Exception {
    if (this.cached == null) return val;
    String value = getString(key);
    if (value == null) return val;
    try {
      T result = JsonUtils.decode(value, glass);
      return result;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T extends DynamicConfigClass> T getDynmicClass(String key, Class<T> glass)
      throws Exception {
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
