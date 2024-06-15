package latte.lib.api.config;

public interface DynamicConfigClass {
  void change(Object now) throws Exception;
}
