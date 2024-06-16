package latte.lib.api.config;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class AbstractDynamicConfigClass implements DynamicConfigClass{
    Map<String, List<BiConsumer>> listen = new LinkedHashMap<>();

    public synchronized void addListen(String key, BiConsumer func) {
        List<BiConsumer> list = this.listen.get(key);
        if (list == null) {
          list = new LinkedList<>();
          this.listen.put(key, list);
        }
        list.add(func);
    }
    public <T> void notify(String key, T old,T now) {
      List<BiConsumer> list = this.listen.get(key);
      if (list == null) return;
      list.forEach(func -> {
        func.accept(old, now);
      });
    }
}
