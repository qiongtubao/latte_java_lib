package latte.lib.kv;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import latte.lib.api.kv.KVClient;
import latte.lib.api.kv.scan.AbstractScanIterator;
import latte.lib.api.monitor.Monitor;
import latte.lib.api.monitor.Transaction;
import latte.lib.common.serialization.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  这个对象处理委托处理monitor外部封装
 */
public class KVClientDelegate implements KVClient {
  KVClient kvClient;

  Monitor monitor;

  Map<String, String> basicTags = new LinkedHashMap<>();
  public KVClientDelegate(KVClient kvClient, Monitor monitor) {
    this.kvClient = kvClient;
    this.monitor = monitor;
  }
  @Override
  public boolean del(String key) throws Exception {
    Transaction transaction = monitor.getTransaction(this.kvClient.getClass().getSimpleName() + ".del");
    boolean result = false;
    try {
       for(Map.Entry<String, String> kv: basicTags.entrySet()) {
         transaction.addTag(kv.getKey(), kv.getValue());
       }
       transaction.addTag("key", key);
       transaction.addTag("method", "del");
       result = kvClient.del(key);
       transaction.setSuccess();
    } catch (Exception e) {
      transaction.setFail(e);
      throw e;
    } finally {
      transaction.complete();
    }
    return result;
  }
  static Logger logger = LoggerFactory.getLogger(KVClientDelegate.class);
  @Override
  public Iterator<String> scanKey(String key, String end, int limit) {
    Transaction transaction = monitor.getTransaction(this.kvClient.getClass().getSimpleName() + ".scanKey");
    Iterator<String> result = null;
    try {
      for(Map.Entry<String, String> kv: basicTags.entrySet()) {
        transaction.addTag(kv.getKey(), kv.getValue());
      }
      transaction.addTag("scan_start", key);
      transaction.addTag("scan_end", end);
      transaction.addTag("limit", String.valueOf(limit));
      transaction.addTag("method", "scanKey");
      Iterator<String> result1 = this.kvClient.scanKey(key, end, limit);
      if (result1 instanceof AbstractScanIterator) {
          result = new ScanIteratorDelegate<>((AbstractScanIterator)result1, monitor, basicTags);
      }

    } catch (Exception e) {
      transaction.setFail(e);
      throw e;
    } finally {
      transaction.complete();
    }
    return result;
  }

  @Override
  public boolean set(String key, String value) throws Exception {
    Transaction transaction = monitor.getTransaction(this.kvClient.getClass().getSimpleName() +".set");
    boolean result = false;
    try {
      for(Map.Entry<String, String> kv: basicTags.entrySet()) {
        transaction.addTag(kv.getKey(), kv.getValue());
      }
      transaction.addTag("key", key);
      transaction.addTag("value", value);
      transaction.addTag("method", "set");
      result = kvClient.set(key, value);
      transaction.setSuccess();
    } catch (Exception e) {
      transaction.setFail(e);
      throw e;
    } finally {
      transaction.complete();
    }
    return result;
  }

  @Override
  public String get(String key) throws Exception {
    Transaction transaction = monitor.getTransaction(this.kvClient.getClass().getSimpleName() +".get");
    String result = null;
    try {
      for(Map.Entry<String, String> kv: basicTags.entrySet()) {
        transaction.addTag(kv.getKey(), kv.getValue());
      }
      transaction.addTag("key", key);
      transaction.addTag("method", "get");
      result = kvClient.get(key);
      transaction.setSuccess();
    } catch (Exception e) {
      transaction.setFail(e);
      throw e;
    } finally {
      transaction.complete();
    }
    return result;
  }

  @Override
  public List<String> mget(List<String> keys) {
    Transaction transaction = monitor.getTransaction(this.kvClient.getClass().getSimpleName() +".mget");
    List<String> result = null;
    try {
      for(Map.Entry<String, String> kv: basicTags.entrySet()) {
        transaction.addTag(kv.getKey(), kv.getValue());
      }
      transaction.addTag("keys", JsonUtils.encode(keys));
      transaction.addTag("method", "mget");
      result = kvClient.mget(keys);
      transaction.setSuccess();
    } catch (Exception e) {
      transaction.setFail(e);
    } finally {
      transaction.complete();
    }
    return result;
  }

  @Override
  public boolean mset(Map<String, String> map) {
    Transaction transaction = monitor.getTransaction(this.kvClient.getClass().getSimpleName() +".mset");
    boolean result = false;
    try {
      for(Map.Entry<String, String> kv: basicTags.entrySet()) {
        transaction.addTag(kv.getKey(), kv.getValue());
      }
      transaction.addTag("keys", JsonUtils.encode(map));
      transaction.addTag("method", "mset");
      result = kvClient.mset(map);
      transaction.setSuccess();
    } catch (Exception e) {
      transaction.setFail(e);
    } finally {
      transaction.complete();
    }
    return result;
  }
  public KVClientDelegate addBasicTag(String k, String v) {
    basicTags.put(k,v);
    return this;
  }

  @Override
  public void close() throws Exception {
    kvClient.close();
  }
}
