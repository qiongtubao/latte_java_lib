package latte.lib.db.example.tikv.api;

import latte.lib.tikv.KVClient;
import latte.lib.tikv.KVClientFactory;
import latte.lib.tikv.api.StringCommand;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class StringCommandTest {

    @Test
    public void set_test() {
        StringCommand client = KVClientFactory.getKVClientByClusterName("local");
        client.set("aa", "bb");
        Assert.assertEquals("bb",client.get("aa"));
    }

    @Test
    public void mset_test() {
        StringCommand client = KVClientFactory.getKVClientByClusterName("local");
        client.mset("aa", "v1", "bb", "v11");
        List<String> values = client.mget("aa", "bb");
        Assert.assertEquals(2, values.size());
        Assert.assertEquals("v1", values.get(0));
        Assert.assertEquals("v11", values.get(1));
    }


}
