package latte.lib.tikv;

import latte.lib.tikv.plugin.TiKVComponents;
import org.junit.After;
import org.junit.Before;

public class AbstractKVServer {
    public String pdAddr = "127.0.0.1:12379";
    @Before
    public void before() throws Exception {
        if (!AllTests.startAllTest) {
            TiKVComponents.getSingleton().startPd(12379);
            TiKVComponents.getSingleton().startTiKV(30160, 12379);
        }
    }

    @After
    public void after() throws Exception {
        if (!AllTests.startAllTest) {
            TiKVComponents.getSingleton().stopTiKV(30160);
            TiKVComponents.getSingleton().stopPd(12379);
        }
    }
}
