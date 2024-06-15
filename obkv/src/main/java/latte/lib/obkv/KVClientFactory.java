package latte.lib.obkv;

import java.util.LinkedHashMap;
import java.util.Map;
import latte.lib.obkv.impl.DefaultKVClient;

public class KVClientFactory {
  private Map<String, KVClient> clients = new LinkedHashMap<>();
//  public static KVClient getKVClient(String pdAddr) {
//    TiSession session = sessions.get(pdAddr);
//    if (session != null) {
//      return new DefaultKVClient(session);
//    }
//    synchronized (sessions) {
//      session = sessions.get(pdAddr);
//      DefaultKVClient client = new DefaultKVClient(configAddr);
//    }
//
//  }
}
