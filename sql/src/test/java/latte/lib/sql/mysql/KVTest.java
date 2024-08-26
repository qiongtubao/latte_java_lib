package latte.lib.sql.mysql;

import java.util.List;
import java.util.Map;
import latte.lib.api.db.sql.SqlClient;
import latte.lib.api.db.sql.annotation.Id;
import latte.lib.api.db.sql.annotation.Table;
import latte.lib.sql.SqlParser;
import latte.lib.sql.User;
import latte.lib.sql.UserTest;
import latte.lib.sql.sqlite.SqliteClient;
import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KVTest {
  protected SqlClient getClient() throws Exception {
    return MysqlClient
        .createMysqlClient(
            "bbzmysqltest.mysql.db.fat.qa.nt.ctripcorp.com",
                55111,
          "bbzmysqltestdb",
          "m_bbzmysqltest_w",
          "aA1^wSWsOZNbrgLxJe37"
        );
  }
  @Table(name = "kv_table")
  @Getter
  @Setter
  static class KV {
      @Id
      String k;
      String v;
  }
  boolean printSql = true;
  static Logger logger = LoggerFactory.getLogger(UserTest.class);

  @Test
  public void delete() throws Exception {
    SqlClient client = getClient();
    SqlParser<KV> parser = new SqlParser(KV.class);
    KV kv = new KV();
    kv.k = "test";
    String sql = parser.delete(kv);
    if (printSql) {
      logger.info("[sql] delete sql :{}", sql);
    }
    client.exec(sql);
  }
  @Test
  public void test() throws Exception {
    SqlClient client = getClient();
    SqlParser<KV> parser = new SqlParser(KV.class);
    KV selectKV = new KV();
    selectKV.k = "test";
    String sql = parser.select(selectKV);
    if (printSql) {
      logger.info("[sql] select sql :{}", sql);
    }
    List<Map<String, Object>> rs = client.select(sql);
    Assert.assertEquals(0, rs.size());

    KV kv = new KV();
    kv.k  = "test";
    kv.v = "v0";
    sql = parser.insert(kv);
    if (printSql) {
      logger.info("[sql] insert sql :{}", sql);
    }
    boolean result = client.exec(sql);
    System.out.println(result);

    rs = client.select(parser.select(selectKV));
    Assert.assertEquals(1, rs.size());

  }

}
