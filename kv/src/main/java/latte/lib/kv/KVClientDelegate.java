package latte.lib.kv;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import latte.lib.api.kv.KVClient;
import latte.lib.api.kv.scan.AbstractScanIterator;
import latte.lib.api.monitor.Monitor;
import latte.lib.api.monitor.Transaction;
import latte.lib.common.serialization.JsonUtils;

/**
 *  这个对象处理委托处理monitor外部封装
 */
public class KVClientDelegate implements KVClient {
  KVClient kvClient;

  Monitor monitor;
  public KVClientDelegate(KVClient kvClient, Monitor monitor) {
    this.kvClient = kvClient;
    this.monitor = monitor;
  }
  @Override
  public boolean del(String key) throws Exception {
    Transaction transaction = monitor.getTransaction(this.kvClient.getClass().getSimpleName() + ".del");
    boolean result = false;
    try {
       result = kvClient.del(key);
       transaction.addTag("key", key);
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
  public Iterator<String> scanKey(String key, String end, int limit) {
    Transaction transaction = monitor.getTransaction(this.kvClient.getClass().getSimpleName() + ".scanKey");
    Iterator<String> result = null;
    try {

      result = kvClient.scanKey(key, end, limit);
      if (result instanceof  AbstractScanIterator) {
        result = new ScanIteratorDelegate<>((AbstractScanIterator)result, monitor);
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
      result = kvClient.set(key, value);
      transaction.addTag("key", key);
      transaction.addTag("value", value);
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
      result = kvClient.get(key);
      transaction.addTag("key", key);
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
      result = kvClient.mget(keys);
      transaction.addTag("keys", JsonUtils.encode(keys));
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
      result = kvClient.mset(map);
      transaction.addTag("keys", JsonUtils.encode(map));
      transaction.setSuccess();
    } catch (Exception e) {
      transaction.setFail(e);
    } finally {
      transaction.complete();
    }
    return result;
  }
}
