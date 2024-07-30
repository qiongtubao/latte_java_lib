package latte.lib.kv.obkv;

import com.alipay.oceanbase.rpc.ObTableClient;
import com.alipay.oceanbase.rpc.property.Property;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import latte.lib.api.kv.KVClient;
import latte.lib.kv.obkv.impl.DefaultObkvClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObkvClientFactory {
  //Map<ConfigserverAddr, Map<tableName, client>>
  Map<ObkvClientInfo,  ObTableClient> clients = new LinkedHashMap<>();

  Logger logger = LoggerFactory.getLogger(ObkvClientFactory.class);

  static ObTableClient createObTableClient(ObkvClientInfo info) throws Exception {
    ObTableClient client = new ObTableClient();
    client.setParamURL(info.getConfigServerAddr());
    client.setDatabase(info.getDatabase());
    client.setFullUserName(info.getFullUserName());
    client.setSysUserName(info.getSysUserName());
    client.setPassword(info.getPassword());
    client.setSysPassword(info.getSysPassword());
    Map<String,String> properties = info.getProperties();
    if (properties != null && properties.size() != 0) {
      for (Entry<String, String> kv : info.getProperties().entrySet()) {
        client.addProperty(kv.getKey(), kv.getValue());
      }
    }
//        client.setRpcExecuteTimeout(10000);
//        client.setRuntimeMaxWait(10000);
    client.init();
    return client;
  }
  synchronized ObTableClient get(ObkvClientInfo info) throws Exception {
      ObTableClient client = clients.get(info);
      if (client == null) {
        client = createObTableClient(info);
        clients.put(info, client);
      }
      return client;
  }

  static private ObkvClientFactory instance= new ObkvClientFactory();
  static public KVClient getClient(ObkvClientInfo info, String tableName) throws Exception {
    ObTableClient obTableClient = instance.get(info);
    return new DefaultObkvClient(obTableClient, tableName);
  }

  static public KVClient createClient(ObkvClientInfo info, String tableName) throws Exception {
    return new DefaultObkvClient(createObTableClient(info), tableName);
  }
}
