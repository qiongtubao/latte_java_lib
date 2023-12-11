package latte.lib.db.example.tikv.api;

import latte.lib.tikv.KVClient;
import latte.lib.tikv.KVClientFactory;
import latte.lib.tikv.api.StoreType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CommonCommandTest {

    // ===== del ======
    @Test
    public void string_del_test() {
        KVClient client = KVClientFactory.getKVClientByClusterName("local");
        client.mset("string1", "1.v1", "string12", "2.v1");
        client.del(StoreType.String, "string1");
        Assert.assertEquals(null, client.get("string1"));
        Assert.assertEquals("2.v1", client.get("string12"));
    }

    @Test
    public void hash_del_test() {
        KVClient client = KVClientFactory.getKVClientByClusterName("local");
        client.hmset("hash_test","aa", "v0", "bb", "v0");
        client.hmset("hash_testa", "a", "v0");
        client.del(StoreType.Hash, "hash_test");
        Assert.assertEquals(null,client.hget("hash_test","aa"));
        Assert.assertEquals("v0",client.hget("hash_testa","a"));
    }
    // ===== scan ======

    @Test
    public void scan_string_test() {
        KVClient client = KVClientFactory.getKVClientByClusterName("local");
        client.mset("a","v1", "a0", "v1", "b", "v0");
        Iterator<String> iter = client.scan(StoreType.String, "a", "b");
        List<String> keys = new LinkedList<>();
        while(iter.hasNext()) {
            String key = iter.next();
            keys.add(key);
        }
        Assert.assertEquals(2 , keys.size());
        Assert.assertEquals("a" , keys.get(0));
        Assert.assertEquals("a0" , keys.get(1));
    }

    @Test
    public void scan_hash_test() {
        KVClient client = KVClientFactory.getKVClientByClusterName("local");
        client.hmset("hash","aa", "v0", "bb", "v0");
        client.hmset("hash0", "a", "v0");
        client.hmset("hash1", "b", "v0");
        Iterator<String> iter = client.scan(StoreType.Hash, "hash", "hash1");
        List<String> keys = new LinkedList<>();
        while(iter.hasNext()) {
            String key = iter.next();
            keys.add(key);
        }
        Assert.assertEquals(2 , keys.size());
        Assert.assertEquals("hash" , keys.get(0));
        Assert.assertEquals("hash0" , keys.get(1));
    }
}
