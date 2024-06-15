package latte.lib.api.config;

public interface DynamicConfigClass extends DynamicChange {
  void change(Object now) throws Exception;
}
