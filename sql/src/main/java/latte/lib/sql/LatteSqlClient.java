package latte.lib.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import latte.lib.api.db.sql.SqlClient;

public abstract class LatteSqlClient implements SqlClient {
    Connection conn;
    public LatteSqlClient(Connection conn) {
      this.conn = conn;
    }


    @Override
    public boolean exec(String sql) {
      try (Statement stmt = conn.createStatement()) {
        if(!stmt.execute(sql)) {
          return stmt.getUpdateCount() == 0? false: true;
        } else {
          throw  new RuntimeException("[sqlClient] select sql should use select function: " + sql);
        }
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


  @Override
  public void close() throws Exception {
      conn.close();
  }
}
