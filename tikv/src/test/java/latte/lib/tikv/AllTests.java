package latte.lib.tikv;

import latte.lib.tikv.api.StringCommandTest;
import latte.lib.tikv.plugin.TiKVComponents;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        StringCommandTest.class
})
public class AllTests {
    static boolean startAllTest = false;
    @BeforeClass
    public static void startTiKV() throws Exception {
        System.out.println("startTiKV");
        TiKVComponents.getSingleton().startPd(12379);
        TiKVComponents.getSingleton().startTiKV(30160, 12379);
        startAllTest = true;
    }

    @AfterClass
    public static void stopTiKV() throws Exception {
        TiKVComponents.getSingleton().stopTiKV(30160);
        TiKVComponents.getSingleton().stopPd(12379);
        startAllTest = false;

    }
}
