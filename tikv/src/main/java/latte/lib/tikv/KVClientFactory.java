package latte.lib.tikv;

import latte.lib.tikv.impl.DefaultKVClient;
import org.tikv.common.TiConfiguration;
import org.tikv.common.TiSession;

import java.util.LinkedHashMap;
import java.util.Map;

public class KVClientFactory {
    static Map<String, TiSession> sessions = new LinkedHashMap<>();
    public static KVClient getKVClientByClusterName(String clusterName) {
        String pd = "";
        if (clusterName == "local") {
            pd ="127.0.0.1:12379";
        }
        TiSession session = sessions.get(clusterName);
        if (session != null) {
            return new DefaultKVClient(session);
        }
        synchronized (sessions) {
            session = sessions.get(clusterName);
            if (session != null) {
                return new DefaultKVClient(session);
            }
            TiConfiguration conf = TiConfiguration.createDefault(pd);
            conf.setApiVersion(TiConfiguration.ApiVersion.V2);
            session =TiSession.create(conf);
            if (session == null) {
                return null;
            }
            sessions.put(clusterName, session);
            return new DefaultKVClient(session);
        }
    }
}
