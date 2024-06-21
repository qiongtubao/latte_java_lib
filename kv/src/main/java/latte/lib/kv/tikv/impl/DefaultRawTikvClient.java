package latte.lib.kv.tikv.impl;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import latte.lib.api.kv.KVClient;
import latte.lib.api.kv.scan.AbstractScanIterator;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tikv.common.operation.iterator.ConcreteScanIterator;
import org.tikv.common.util.BackOffer;
import org.tikv.common.util.ConcreteBackOffer;
import org.tikv.kvproto.ImportKvpb.KVPair;
import org.tikv.kvproto.Kvrpcpb.KvPair;
import org.tikv.raw.RawKVClient;
import org.tikv.shade.com.google.protobuf.ByteString;


@Setter
@Getter
public class DefaultRawTikvClient implements KVClient {

  RawKVClient client;
  public DefaultRawTikvClient(RawKVClient client) {
    this.client = client;
  }
  @Override
  public boolean set(String key, String value) throws Exception {
     client.put(ByteString.copyFromUtf8(key), ByteString.copyFromUtf8(value));
     return true;
  }

  @Override
  public String get(String key) throws Exception{
    Optional<ByteString> result = client.get(ByteString.copyFromUtf8(key));
    if (!result.isPresent()) {
      return null;
    }
    return result.get().toStringUtf8();
  }

  @Override
  public List<String> mget(List<String> keys) throws Exception {
    return client.batchGet(keys.stream().map(key -> ByteString.copyFromUtf8(key)).collect(Collectors.toList())).stream().map(kv -> {
      return kv.getValue().toStringUtf8();
    }).collect(Collectors.toList());
  }

  @Override
  public boolean mset(Map<String, String> map) throws Exception {
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
    client.delete(ByteString.copyFromUtf8(key));
    return true;
  }

  static class RawTikvScanIterator extends AbstractScanIterator {

    String indexKey;

    RawKVClient client;
    public RawTikvScanIterator(RawKVClient kvClient, String startKey, String endKey, int limit) {
      super(startKey, endKey, limit);
      this.client = kvClient;
      indexKey = startKey;
    }

    public String nextString(String str) {
      if (str == null || str.isEmpty()) {
        return str;
      }

      char[] charArray = str.toCharArray();
      char lastChar = charArray[charArray.length - 1];
      char modifiedChar = (char) (lastChar + 1);

      charArray[charArray.length - 1] = modifiedChar;
      return new String(charArray);

    }
    @Override
    public int queryData() {
      List<KvPair> result = this.client.scan(
          ByteString.copyFromUtf8(indexKey),
          ByteString.copyFromUtf8(endKey),
      limit);
      int len = 0;
      for(KvPair kv : result) {
        String key = (kv.getKey().toStringUtf8());
        data.add(key);
        len++;
      }
      if (len == 0) return 0;
      //可能会出现炸尸情况
      indexKey = nextString((String)data.get(len-1));
      return len;
    }
  }

  @Override
  public Iterator<String> scanKey(String key, String end, int limit) {
    return new RawTikvScanIterator(client, key, end, limit);
  }
}
