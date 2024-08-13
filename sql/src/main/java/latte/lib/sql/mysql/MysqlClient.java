package latte.lib.sql.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import latte.lib.api.db.sql.SqlClient;
import latte.lib.sql.LatteSqlClient;
import latte.lib.sql.sqlite.SqliteClient;

public class MysqlClient extends LatteSqlClient {

  public MysqlClient(Connection conn) {
    super(conn);
  }

  public static MysqlClient createSqliteClient(String address, int port, String database, String username, String password) throws Exception {
    String url = String.format("jdbc:mysql://%s:%d/%s", address, port, database);
    Connection conn = null;
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      // 加载并建立数据库连接
      conn = DriverManager.getConnection(url, username, password);
    } catch (SQLException e) {
      throw e;
    }
    return new MysqlClient(conn);
  }
  @Override
  public boolean supportInsertOrUpdate() {
    return true;
  }
}
