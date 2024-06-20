package latte.lib.kv.obkv.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class DefaultObkvClientTest extends AbstractObkvClientTest {
  @Test
  public void scan() {
    this.client.set("a", "1");
    this.client.set("b", "2");
    Iterator<String> result = this.client.scanKey("a", "c", 1);
    List<String> list = new LinkedList<>();
    while(result.hasNext()) {
      String key = result.next();
      list.add(key);
    }
    Assert.assertEquals(2, list.size());
    list.sort((a, b) -> {
      return a .compareTo(b);
    });
    Assert.assertEquals("a",list.get(0));
    Assert.assertEquals("b",list.get(1));
  }
}
