package latte.lib.kv.tikv;

import java.util.LinkedHashMap;
import java.util.Map;
import latte.lib.api.kv.KVClient;
import latte.lib.kv.obkv.ObkvClientInfo;
import latte.lib.kv.obkv.impl.DefaultObkvClient;
import org.tikv.common.TiSession;

public class TikvClientFactory {
  public Map<TikvClientInfo, TiSession> sessions = new LinkedHashMap<>();


  public synchronized TiSession get(TikvClientInfo clientInfo) {
    TiSession session = sessions.get(clientInfo);
    if (session == null) {
      session = clientInfo.getType().createTiSession(clientInfo.getPdAddr(), clientInfo.getTimeout());
      sessions.put(clientInfo, session);
    }
    return session;
  }
  static private TikvClientFactory instance= new TikvClientFactory();
  static public KVClient getClient(TikvClientInfo clientInfo) {
    TiSession tiSession = instance.get(clientInfo);
    return clientInfo.getType().getClient(tiSession);
  }

  static public KVClient createClient(TikvClientInfo info) throws Exception {
    return info.getType().getClient(info.getType().createTiSession(info.getPdAddr(), info.getTimeout()));
  }

}
