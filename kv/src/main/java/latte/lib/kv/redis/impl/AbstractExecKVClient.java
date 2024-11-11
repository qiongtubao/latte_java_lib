package latte.lib.kv.redis.impl;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import latte.lib.api.kv.KVClient;
import latte.lib.tikv.impl.AbstractKVClient;

public abstract class AbstractExecKVClient implements KVClient {

  protected abstract Object execCommand(String... args)
      throws ExecutionException, InterruptedException;
  @Override
  public boolean del(String key) throws Exception {
    return (boolean) execCommand("del", key);
  }



  @Override
  public Iterator<String> scanKey(String key, String end, int limit) {
    return null;
  }

  @Override
  public boolean set(String key, String value) throws Exception {
    return new String((byte[])this.execCommand("set", key, value), StandardCharsets.UTF_8).equals("OK");
  }

  @Override
  public String get(String key) throws Exception {
    Object result = execCommand("get",key);
    if (result == null) return null;
    return new String((byte[])result);
  }

  @Override
  public List<String> mget(List<String> keys) throws Exception {
    return null;
  }

  @Override
  public boolean mset(Map<String, String> map) throws Exception {
    return false;
  }
}
