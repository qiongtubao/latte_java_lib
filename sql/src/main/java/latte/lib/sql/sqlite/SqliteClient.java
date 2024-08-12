package latte.lib.sql.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import latte.lib.api.db.sql.SqlClient;
import latte.lib.sql.LatteSqlClient;

public class SqliteClient extends LatteSqlClient {


  public SqliteClient(Connection conn) {
    super(conn);
  }

  static SqliteClient createSqliteClient(String fileName) throws Exception {
    String url = String.format("jdbc:sqlite:%s", fileName);
    Connection conn = null;
    try {
      // 加载并建立数据库连接
      conn = DriverManager.getConnection(url);
    } catch (SQLException e) {
      throw e;
    }
    return new SqliteClient(conn);
  }

  @Override
  public boolean supportInsertOrUpdate() {
    return false;
  }
}
