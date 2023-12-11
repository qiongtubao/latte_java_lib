package latte.lib.db.example.tikv.api;

import latte.lib.tikv.KVClientFactory;
import latte.lib.tikv.api.HashCommand;
import latte.lib.tikv.api.StringCommand;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

public class HashCommandTest {
        @Test
        public void hset_hdel_test() {
            HashCommand client = KVClientFactory.getKVClientByClusterName("local");
            client.hset("hash_test","aa", "v0");
            Assert.assertEquals("v0",client.hget("hash_test","aa"));
            client.hmdel("hash_test", "aa");
            Assert.assertEquals(null,client.hget("hash_test","aa"));
        }

        @Test
        void hmset_test() {
            HashCommand client = KVClientFactory.getKVClientByClusterName("local");
            client.hmset("hash_test","aa", "v0", "bb", "v00");
            List<String> values =  client.hmget("hash_test", "aa", "bb");
            Assert.assertEquals(2, values.size());
            Assert.assertEquals("v0", values.get(0));
            Assert.assertEquals("v00", values.get(1));
        }

        @Test
        public void hscan_test() {
            HashCommand client = KVClientFactory.getKVClientByClusterName("local");
            client.hmset("hash_test","aa", "v0", "bb", "v0");
            Iterator<String> iter = client.hscan("hash_test");
            String key = iter.next();
            Assert.assertEquals(key, "aa");
            client.hmdel("hash_test", "bb");
            Assert.assertEquals(null, client.hget("hash_test", "bb"));
            key = iter.next();
            Assert.assertEquals(key, "bb");

        }





}
