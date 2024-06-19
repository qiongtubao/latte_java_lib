package latte.lib.kv.tikv.impl;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import latte.lib.api.kv.KVClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tikv.raw.RawKVClient;
import org.tikv.shade.com.google.protobuf.ByteString;

public class DefaultRawTikvClient implements KVClient {

  RawKVClient client;
  public DefaultRawTikvClient(RawKVClient client) {
    this.client = client;
  }
  @Override
  public boolean set(String key, String value) {
     client.put(ByteString.copyFromUtf8(key), ByteString.copyFromUtf8(value));
     return true;
  }

  @Override
  public String get(String key) {
    return client.get(ByteString.copyFromUtf8(key)).get().toStringUtf8();
  }

  @Override
  public List<String> mget(List<String> keys) {
    return client.batchGet(keys.stream().map(key -> ByteString.copyFromUtf8(key)).collect(Collectors.toList())).stream().map(kv -> {
      return kv.getValue().toStringUtf8();
    }).collect(Collectors.toList());
  }

  @Override
  public boolean mset(Map<String, String> map) {
    Map<ByteString, ByteString> bmap = new LinkedHashMap<ByteString, ByteString>();
    for(Entry<String, String> kv: map.entrySet()) {
      bmap.put(ByteString.copyFromUtf8(kv.getKey()), ByteString.copyFromUtf8(kv.getValue()));
    }
    client.batchPut(bmap);
    return true;
  }

  Logger logger = LoggerFactory.getLogger(DefaultRawTikvClient.class);
  @Override
  public boolean del(String key) throws Exception {
    try {
      client.delete(ByteString.copyFromUtf8(key));
    } catch (Exception e) {
      logger.error("[tikv-del] del fail key {},", e);
      return false;
    }
    return true;
  }
}
