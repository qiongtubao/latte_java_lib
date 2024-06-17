package latte.lib.obkv;

import com.alipay.oceanbase.rpc.ObTableClient;
import java.util.LinkedHashMap;
import java.util.Map;
import latte.lib.obkv.impl.DefaultKVClient;

public class KVClientFactory {
  private Map<String, ObTableClient> clients = new LinkedHashMap<>();
//  public static KVClient getKVClient(String configServerAddr) {
//    ObTableClient obClient = new ObTableClient();
//    obClient.setParamURL(configServerAddr);
//    obClient.
//
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
