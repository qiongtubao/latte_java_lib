package latte.lib.tikv.api;

import latte.lib.tikv.AbstractKVServer;
import latte.lib.tikv.KVClient;
import latte.lib.tikv.KVClientFactory;
import latte.lib.tikv.commiter.KVTwoPhaseCommiter;
import latte.lib.tikv.impl.DefaultKVClient;
import latte.lib.tikv.impl.KVTransaction;
import org.junit.Assert;
import org.junit.Test;

public class TwoPcCommitTest extends AbstractKVServer {
  /* 当prewrite 但没commit 时 get获得的值是什么？*/
  @Test
  public void AfterPreWriteGet() {
    DefaultKVClient client = (DefaultKVClient)KVClientFactory.getKVClient(pdAddr);

    client.set("key", "value");
    String value = client.get("key");
    Assert.assertEquals("value", value);
    //
    KVTransaction transaction = client.beginOptimistic();
    transaction.set("key", "value1");
    transaction.set("key1", "value2");
    KVTwoPhaseCommiter commiter =  (KVTwoPhaseCommiter)transaction.getCommiter();
    commiter.prewritePrimaryKey(1000);
    value = client.get("key");
    Assert.assertEquals("value", value);
    commiter.prewriteSecondaryKeys(1000);
    value = client.get("key");
    Assert.assertEquals("value", value);

    commiter.commitPrimaryKey(1000);
    value = client.get("key");
    Assert.assertEquals("value1", value);
    value = client.get("key1");
    Assert.assertEquals(null, value);
    commiter.commitSecondaryKeys(1000);
    value = client.get("key1");
    Assert.assertEquals("value2", value);




  }
}
