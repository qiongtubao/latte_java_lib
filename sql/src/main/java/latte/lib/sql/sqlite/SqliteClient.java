package latte.lib.sql.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
