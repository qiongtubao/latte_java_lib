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

public class SqliteClient implements SqlClient {
  Connection conn = null;
  public SqliteClient(Connection conn) {
    this.conn = conn;
  }


  @Override
  public boolean exec(String sql) {
    try (Statement stmt = conn.createStatement()) {
      return stmt.execute(sql);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
  @Override
  public List<Map<String,Object>> select(String sql) {
    try (Statement stmt = conn.createStatement()) {
      ResultSet rs = stmt.executeQuery(sql);
      List list = new LinkedList();
      ResultSetMetaData metaData = rs.getMetaData();
      int columnCount = metaData.getColumnCount();
      while (rs.next()) {
        Map<String,Object> map = new LinkedHashMap<>();
        for (int i = 1; i <= columnCount; i++) {
          String key = metaData.getColumnName(i);
          Object value = rs.getObject(key);
          map.put(key, value);
        }
        list.add(map);
      }
      rs.close();
      return list;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  static SqliteClient createSqliteClient(String dbUrl) throws Exception {
    Connection conn = null;
    try {
      // 加载并建立数据库连接
      conn = DriverManager.getConnection(dbUrl);
    } catch (SQLException e) {
      throw e;
    }
    return new SqliteClient(conn);
  }
}
