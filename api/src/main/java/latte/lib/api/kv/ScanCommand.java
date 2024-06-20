package latte.lib.api.kv;

import java.util.Iterator;

public interface ScanCommand {
  Iterator<String> scanKey(String key, String end, int limit);

}
