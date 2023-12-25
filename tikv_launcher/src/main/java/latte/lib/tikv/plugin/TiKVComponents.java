package latte.lib.tikv.plugin;

import latte.lib.tikv.plugin.impl.PdServer;
import latte.lib.tikv.plugin.impl.TiKVServer;

import java.util.LinkedHashMap;
import java.util.Map;

public class TiKVComponents {
    Map<Integer, PdServer> pds = new LinkedHashMap<>();
    Map<Integer, TiKVServer> tikvs = new LinkedHashMap<>();

    public void stopTiKV(int port) throws Exception {
        TiKVServer tiKVServer = tikvs.get(port);
        if (tiKVServer == null) return;
        synchronized (tikvs) {
            tiKVServer = tikvs.get(port);
            if (tiKVServer == null) return;
            tiKVServer.stop();
            tikvs.remove(port);
        }
    }

    public PdServer startPd(int port) throws Exception {
        PdServer pdServer = pds.get(port);
        if (pdServer != null) return pdServer;
        synchronized (pds) {
            pdServer = pds.get(port);
            if (pdServer != null) return pdServer;
            pdServer = new PdServer(port);
            pdServer.start();
            pds.put(port, pdServer);
            return pdServer;
        }
    }

    public TiKVServer startTiKV(int port, int pd_port) throws Exception {
        TiKVServer tiKVServer = tikvs.get(port);
        if (tiKVServer != null) return tiKVServer;
        synchronized (tikvs) {
            tiKVServer = tikvs.get(port);
            if (tiKVServer != null) return tiKVServer;
            tiKVServer = new TiKVServer(port, pd_port);
            tiKVServer.start();
            tikvs.put(port, tiKVServer);
            return tiKVServer;
        }
    }
    public void stopPd(int port) throws Exception {
        PdServer pdServer = pds.get(port);
        if (pdServer == null) return;
        synchronized (pds) {
            pdServer = pds.get(port);
            if (pdServer == null) return;
            pdServer.stop();
            pds.remove(port);
        }
    }

    static private final TiKVComponents single = new TiKVComponents();
    public static TiKVComponents getSingleton() {
        return single;
    }

}
