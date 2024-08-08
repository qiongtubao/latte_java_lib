package latte.lib.sql.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import latte.lib.api.db.sql.SqlClient;
import latte.lib.sql.SqlParser;
import latte.lib.sql.User;
import org.junit.Test;

public class UserTest extends latte.lib.sql.UserTest {
    @Test
    public void createTable() throws Exception {
       super.createTable();
    }
  private static final String DB_URL = "jdbc:sqlite:test.db";
  @Override
  protected SqlClient getClient() throws Exception {
      return SqliteClient
          .createSqliteClient(DB_URL);
  }

  @Test
  public void insert() throws Exception {
      super.insert();
  }
}
