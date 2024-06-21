package latte.lib.kv.tikv.impl;


import latte.lib.api.kv.KVClient;
import latte.lib.kv.CreateKVClientTest;
import latte.lib.kv.tikv.TikvClientFactory;
import latte.lib.kv.tikv.TikvClientInfo;
import latte.lib.kv.tikv.TikvClientInfo.TikvType;
import org.junit.Before;

public interface CreateTikvClientTest extends CreateKVClientTest {

  default KVClient createClient() throws Exception {
    TikvClientInfo info = new TikvClientInfo();
    info.setType(TikvType.RAW);
    info.setPdAddr("127.0.0.1:12379");
    return TikvClientFactory.createClient(info);
  }
}
