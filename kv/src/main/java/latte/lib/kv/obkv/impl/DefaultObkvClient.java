package latte.lib.kv.obkv.impl;

import static com.alipay.oceanbase.rpc.mutation.MutationFactory.colVal;
import static com.alipay.oceanbase.rpc.mutation.MutationFactory.row;

import com.alipay.oceanbase.rpc.ObTableClient;
import com.alipay.oceanbase.rpc.mutation.BatchOperation;
import com.alipay.oceanbase.rpc.mutation.InsertOrUpdate;
import com.alipay.oceanbase.rpc.mutation.result.BatchOperationResult;
import com.alipay.oceanbase.rpc.mutation.result.MutationResult;
import com.alipay.oceanbase.rpc.stream.QueryResultSet;
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
import latte.lib.api.monitor.Monitor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/* 测试表schema:
CREATE TABLE IF NOT EXISTS `kv_table` (
    `key` varchar(100) NOT NULL,
    `val` varchar(10000) DEFAULT NULL,
    PRIMARY KEY (`key`)
);
*/

@Setter
@Getter
public class DefaultObkvClient implements KVClient {
  ObTableClient client;
  String tableName;

  Monitor monitor;

  public DefaultObkvClient(ObTableClient client, String tableName) {
    this.client = client;
    this.tableName = tableName;
    this.client.addRowKeyElement(tableName, new String[]{"key"});
  }


  String valName = "val";
  Logger logger = LoggerFactory.getLogger(DefaultObkvClient.class);
  @Override
  public boolean set(String key, String value) throws Exception {
//      long rows = client.insertOrUpdate(tableName, key, new String[]{valName}, new Object[]{value});
      MutationResult result = client.insertOrUpdate(tableName).setRowKey(row(colVal("key", key.getBytes(StandardCharsets.UTF_8))))
          .addMutateColVal(colVal(valName, value.getBytes(StandardCharsets.UTF_8))).execute();
//      MutationResult result = client.update(tableName).setRowKey(row(colVal("key", key.getBytes(StandardCharsets.UTF_8))))
//          .addMutateColVal(colVal(valName, value.getBytes(StandardCharsets.UTF_8))).execute();
      if (result.getAffectedRows() != 1) {
        logger.error("[obkv-set]fail to put kv data, rows != 1");
        return false;
      }
      return true;
  }

  @Override
  public String get(String key) throws Exception {
//      TableQuery query = client.query(tableName);
//      query.addScanRange(key, key);
//      QueryResultSet result = query.execute();
//    if (result.next()) {
//      return (String)result.getRow().get(valName);
//    }
      Map<String, Object> result = client.get(tableName, key, new String[]{valName});
      if (result != null && result.size() !=0 ) {
          return (String)result.get(valName);
      }
      return null;
  }

  //再想想 是否使用batch
  @Override
  public List<String> mget(List<String> keys) throws Exception {
     BatchOperation batchOps = client.batchOperation(tableName);
      for(String key: keys) {
        TableQuery query = client.query(tableName);
        query.setRowKey(row(colVal("key", key.getBytes(StandardCharsets.UTF_8))));
        batchOps.addOperation(query);
      }
      BatchOperationResult
          retObj = batchOps.execute();
      if (retObj.size() != keys.size()) {
        logger.error("[obkv-mget]fail to get kv data, rows != 1");
        return new LinkedList<>();
      }
      return retObj.getResults().stream().map(o -> {
        return (String)(((MutationResult) o).getOperationRow().getMap()).get(valName);
      }).collect(Collectors.toList());
  }

  //再想想 是否使用batch
  @Override
  public boolean mset(Map<String, String> map) throws Exception {

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
         logger.error("[obkv-mset]fail to put kv data, rows != 1, {}", map);
         return false;
      }
      return true;
  }

  @Override
  public boolean del(String key) throws Exception {
//      BatchOperation batchOperation = this.client.batchOperation(tableName);
//      Delete deleteOperation = new Delete();
//      deleteOperation.setRowKey(row(colVal("key", key.getBytes(StandardCharsets.UTF_8))));
//      batchOperation.addOperation(deleteOperation);
//      BatchOperationResult result = batchOperation.execute();
//      if (result.getResults().size() == 0) {
//        return false;
//      }
      MutationResult result = this.client.delete(tableName)
//          .setFilter(new ObTableValueFilter(ObCompareOp.EQ, "key", key.getBytes(StandardCharsets.UTF_8)))
          .setRowKey(row(colVal("key", key.getBytes(StandardCharsets.UTF_8))))
          .execute();
      if(result.getAffectedRows() == 0) {
        return false;
      }
      return true;
  }

  @Override
  public void close() throws Exception {
    this.client.close();
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
    public int queryData() {
      TableQuery query = client.query(tableName);
      query.limit(offline, limit);
      query.addScanRange(startKey, endKey);
      int len = 0;
      try {
        QueryResultSet result = query.execute();
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
