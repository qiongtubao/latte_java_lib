package latte.lib.api.db.redis;

public interface StringCommand {
  void set(String key, String value);
  String get(String key);

}
