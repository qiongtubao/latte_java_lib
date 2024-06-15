package latte.lib.obkv.api;

import java.util.List;

public interface StringCommand {
  /* 速度快  查询最新的数据  version = -1 */
  String get(String key) throws Exception;

  void set(String key, String value);
  /* 使用同一个version */
  List<String> mget(String... keys);

  void mset(String... keyorvalue);

}