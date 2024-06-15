package latte.lib.obkv.api;

import java.util.List;
import java.util.Map;

public interface OBKVCommand {
  void batchPut(Map<Byte, Byte> map);
  List<String> batchGet(List<String> list);
}
