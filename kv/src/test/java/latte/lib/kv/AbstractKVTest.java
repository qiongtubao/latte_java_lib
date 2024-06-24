package latte.lib.kv;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import latte.lib.api.kv.KVClient;
import org.junit.Assert;

public abstract class AbstractKVTest implements CreateKVClientTest {
    KVClient client;

    public void before() throws Exception {
      this.client = createClient();
    }


  public void get() throws Exception {
    client.set("a", "1");
    Assert.assertEquals("1", client.get("a"));
    Assert.assertEquals(true, client.del("a"));
    Assert.assertEquals(null, client.get("a"));

  }


  public void scan() throws Exception {
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
    this.client.del("a");
    this.client.del("b");
    this.client.del("d");
  }


  public void mget() throws Exception {
    this.client.set("a", "1");
    this.client.set("b", "2");
    List<String> keys = new LinkedList<>();
    keys.add("a");
    keys.add("b");
    List<String> result = this.client.mget(keys);
    Assert.assertEquals(result.size(), 2);
    Assert.assertEquals(result.get(0), "1");
    Assert.assertEquals(result.get(1), "2");

    this.client.del("a");
    this.client.del("b");
  }


  public void mset() throws Exception {
      Map<String, String> map = new LinkedHashMap<>();
      map.put("a", "msettest1");
      map.put("b", "msettest2");
      map.put("c", "msettest3");
      this.client.mset(map);
      List<String> result = this.client.mget(map.keySet().stream().collect(Collectors.toList()));
      result.sort((a, b) -> {
        return a.compareTo(b);
      });
      Assert.assertEquals(3, result.size());
      Assert.assertEquals("msettest1",result.get(0));
      Assert.assertEquals("msettest2",result.get(1));
      Assert.assertEquals("msettest3",result.get(2));

      this.client.del("a");
      this.client.del("b");
      this.client.del("c");
  }
}
