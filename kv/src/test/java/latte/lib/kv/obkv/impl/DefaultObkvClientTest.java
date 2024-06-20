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
    this.client.set("d", "3");
    Iterator<String> result = this.client.scanKey("a", "c", 1);
    List<String> list = new LinkedList<>();
    while(result.hasNext()) {
      String key = result.next();
      list.add(key);
      System.out.println(key);
    }
    Assert.assertEquals(2, list.size());
    list.sort((a, b) -> {
      return a .compareTo(b);
    });
    Assert.assertEquals("a",list.get(0));
    Assert.assertEquals("b",list.get(1));
  }

  @Test
  public void get() {
    this.client.set("a", "1");
    Assert.assertEquals(this.client.get("a"), "1");
  }

  @Test
  public void mget() {
    this.client.set("a", "1");
    this.client.set("b", "2");
    List<String> keys = new LinkedList<>();
    keys.add("a");
    keys.add("b");
    List<String> result = this.client.mget(keys);
    Assert.assertEquals(result.size(), 2);
    Assert.assertEquals(result.get(0), "1");
    Assert.assertEquals(result.get(1), "2");
  }
}
