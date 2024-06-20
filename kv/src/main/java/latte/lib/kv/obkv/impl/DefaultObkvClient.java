package latte.lib.kv.obkv.impl;

import static com.alipay.oceanbase.rpc.mutation.MutationFactory.colVal;
import static com.alipay.oceanbase.rpc.mutation.MutationFactory.row;

import com.alipay.oceanbase.rpc.ObTableClient;
import com.alipay.oceanbase.rpc.mutation.BatchOperation;
import com.alipay.oceanbase.rpc.mutation.Delete;
import com.alipay.oceanbase.rpc.mutation.InsertOrUpdate;
import com.alipay.oceanbase.rpc.mutation.result.BatchOperationResult;
import com.alipay.oceanbase.rpc.mutation.result.MutationResult;
import com.alipay.oceanbase.rpc.stream.QueryResultSet;
import com.alipay.oceanbase.rpc.table.api.TableBatchOps;
import com.alipay.oceanbase.rpc.table.api.TableQuery;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import latte.lib.api.kv.KVClient;
import latte.lib.api.kv.scan.AbstractScanIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/* 测试表schema:
CREATE TABLE IF NOT EXISTS `kv_table` (
    `key` varchar(100) NOT NULL,
    `val` varchar(10000) DEFAULT NULL,
    PRIMARY KEY (`key`)
);
*/
public class DefaultObkvClient implements KVClient {
  ObTableClient client;
  String tableName;

  public DefaultObkvClient(ObTableClient client, String tableName) {
    this.client = client;
    this.tableName = tableName;
  }


  String valName = "val";
  Logger logger = LoggerFactory.getLogger(DefaultObkvClient.class);
  @Override
  public boolean set(String key, String value) {

    try {
//      long rows = client.insertOrUpdate(tableName, key, new String[]{valName}, new Object[]{value});
      MutationResult result = client.insertOrUpdate(tableName).setRowKey(row(colVal("key", key.getBytes(StandardCharsets.UTF_8))))
          .addMutateColVal(colVal(valName, value.getBytes(StandardCharsets.UTF_8))).execute();
      if (result.getAffectedRows() != 1) {
        logger.error("[obkv-set]fail to put kv data, rows != 1");
        return false;
      }
    } catch (Exception e) {
      logger.error("[obkv-set]fail to put kv data: ", e);
      return false;
    }
    return true;
  }

  @Override
  public String get(String key) {
    try {
      Map<String, Object> result = client.get(tableName, key, new String[]{valName});
      return (String)result.get(valName);
    } catch (Exception e) {
      logger.error("[tikv-get]fail to get kv data {}", e);
      return null;
    }
  }

  //再想想 是否使用batch
  @Override
  public List<String> mget(List<String> keys) {
    try {
      TableBatchOps batchOps = client.batch(tableName);
      for(String key: keys) {
        batchOps.get(key, new String[]{valName});
      }
      List<Object> retObj = batchOps.execute();
      if (retObj.size() != keys.size()) {
        logger.error("[obkv-mget]fail to put kv data, rows != 1");
        return null;
      }
      return retObj.stream().map(o -> {
        return (String)((Map)o).get(valName);
      }).collect(Collectors.toList());
    } catch (Exception e) {
      logger.error("[obkv-mget]fail to put kv data:{}", e);
      return null;
    }
  }

  //再想想 是否使用batch
  @Override
  public boolean mset(Map<String, String> map) {
    try {
      BatchOperation batchOps = client.batchOperation(tableName);
      for(Entry<String, String> kv: map.entrySet()) {
        InsertOrUpdate insertOrUpdate = new InsertOrUpdate();
        insertOrUpdate.setRowKey(row(colVal("key", kv.getKey().getBytes(StandardCharsets.UTF_8))));
        insertOrUpdate.addMutateColVal(colVal(valName, kv.getValue().getBytes(StandardCharsets.UTF_8)));
        batchOps.addOperation(insertOrUpdate);
      }
      BatchOperationResult
          retObj = batchOps.execute();
      if (retObj.size() != map.size()) {
        logger.error("[obkv-mset]fail to put kv data, rows != 1");
         return false;
      }
      return true;
    } catch (Exception e) {
      logger.error("[obkv-mset]fail to put kv data:", e);
      return false;
    }

  }

  @Override
  public boolean del(String key) {
    try {
      BatchOperation batchOperation = this.client.batchOperation(tableName);
      Delete deleteOperation = new Delete();
      deleteOperation.setRowKey(row(colVal("key", key.getBytes(StandardCharsets.UTF_8))));
      batchOperation.addOperation(deleteOperation);
      BatchOperationResult result = batchOperation.execute();
      if (result.size() == 0) {
        return false;
      }
    } catch (Exception e) {
      logger.error("[obkv-delete]fail del kv {}, error:", key, e);
      return false;
    }
    return true;
  }

  static class ObkvScanIterator extends AbstractScanIterator {
    ObTableClient client;
    String tableName;
    int offline = 0;
    public ObkvScanIterator(ObTableClient client, String tableName,
        String startKey, String endKey, int limit) {
      super(startKey, endKey, limit);
      this.client = client;
      this.tableName = tableName;
    }

    @Override
    protected int queryData() {
      TableQuery query = client.query(tableName);
      query.limit(offline, limit);
      int len = 0;
      try {
        QueryResultSet result = query.execute();
        System.out.println(result);
        while(result.next()) {
          data.add((String)result.getRow().get("key"));
          len++;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      offline += len;
      return len;
    }
  }
  @Override
  public Iterator<String> scanKey(String startKey, String endKey, int limit) {
    return new ObkvScanIterator(client, tableName, startKey, endKey, limit);
  }
}
