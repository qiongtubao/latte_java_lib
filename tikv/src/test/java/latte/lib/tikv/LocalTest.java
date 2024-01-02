package latte.lib.tikv;

import latte.lib.tikv.plugin.TiKVComponents;
import org.junit.Test;

public class LocalTest {
    @Test
    public void startTiKV() throws Exception {
        TiKVComponents.getSingleton().startPd(12379);
        TiKVComponents.getSingleton().startTiKV(30160, 12379);
    }

    @Test
    public void stopTiKV() throws Exception {
        TiKVComponents.getSingleton().stopTiKV(30160);
        TiKVComponents.getSingleton().stopPd(12379);
    }
}
