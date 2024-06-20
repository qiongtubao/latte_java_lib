package latte.lib.kv.obkv.impl;

import latte.lib.kv.obkv.ObkvClientFactory;
import latte.lib.kv.obkv.ObkvClientInfo;
import org.junit.Before;

public abstract class AbstractObkvClientTest {

    protected DefaultObkvClient client;
    @Before
    public void before() throws Exception {
      ObkvClientInfo info = new ObkvClientInfo();
      info.setConfigServerAddr("http://172.17.0.1:8080/services?Action=ObRootServiceInfo&ObCluster=obcluster&version=2&ObClusterId=1&database=test");
      info.setDatabase("test");
      info.setFullUserName("root@sys#obcluster");
      info.setPassword("");
      info.setSysUserName("root@sys");
      info.setPassword("");
      client = (DefaultObkvClient) ObkvClientFactory.createClient(info, "kv_table");
    }
}
