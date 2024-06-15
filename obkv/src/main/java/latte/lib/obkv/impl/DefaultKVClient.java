package latte.lib.obkv.impl;


import com.alipay.oceanbase.rpc.ObTableClient;

import java.util.List;
import java.util.Map;
import latte.lib.obkv.KVClient;

public class DefaultKVClient implements KVClient {
  private ObTableClient obTableClient = null;
  private String tableName;
  private String valProperties = "val";
  public DefaultKVClient(ObTableClient obTableClient) {
    this.obTableClient = obTableClient;
  }

  @Override
  public String get(String key) {
    try {
      Map<String,Object> result = obTableClient.get(tableName, key, new String[]{valProperties});
      return (String)result.get(valProperties);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public void set(String key, String value) {

  }

  @Override
  public List<String> mget(String... keys) {
    return null;
  }

  @Override
  public void mset(String... keyorvalue) {

  }
}
