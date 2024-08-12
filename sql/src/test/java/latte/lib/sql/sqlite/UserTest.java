package latte.lib.sql.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import latte.lib.api.db.sql.SqlClient;
import latte.lib.sql.SqlParser;
import latte.lib.sql.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserTest extends latte.lib.sql.UserTest {
    @Test
    public void createTable() throws Exception {
       super.createTable();
    }
  private static final String DB_FILE = "jdbc:sqlite:test.db";
  @Override
  protected SqlClient getClient() throws Exception {
      return SqliteClient
          .createSqliteClient(DB_FILE);
  }

  @Test
  public void insert() throws Exception {
      super.insert();
  }

  @Test
  public void update() throws Exception {
    super.update();
  }

  @Test
  public void insertOrUpdate() throws Exception {
    super.insertOrUpdate();
  }

  @Test
  public void query() throws Exception {
    super.query();
  }

  @Test
  public void delete() throws Exception {
    super.delete();
  }

  @Test
  public void all()  throws Exception{
    createTable();
    super.all();
  }
}
