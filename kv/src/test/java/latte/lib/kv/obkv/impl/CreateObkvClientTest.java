package latte.lib.kv.obkv.impl;

import java.util.HashMap;
import latte.lib.api.kv.KVClient;
import latte.lib.kv.CreateKVClientTest;
import latte.lib.kv.obkv.ObkvClientFactory;
import latte.lib.kv.obkv.ObkvClientInfo;


public  interface CreateObkvClientTest extends CreateKVClientTest {

    default KVClient createClient() throws Exception {
      ObkvClientInfo info = new ObkvClientInfo();
      info.setConfigServerAddr("http://172.17.0.1:8080/services?Action=ObRootServiceInfo&ObCluster=obcluster&version=2&ObClusterId=1&database=test");
      info.setDatabase("test");
      info.setFullUserName("root@sys#obcluster");
      info.setPassword("");
      info.setSysUserName("root@sys");
      info.setPassword("");
      info.setProperties(new HashMap<>());
      return  ObkvClientFactory.createClient(info, "kv_table");
    }
}
