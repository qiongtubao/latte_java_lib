package latte.lib.sql;

import latte.lib.api.db.sql.SqlClient;
import org.junit.Test;

public abstract class UserTest {
    protected abstract SqlClient getClient() throws Exception ;
    String createTableSql() {
        SqlParser parser = new SqlParser(User.class);
        String sql = parser.createTable();
        return sql;
    }

    public void createTable() throws Exception  {
        SqlClient client = getClient();
        client.exec(createTableSql());
    }
}
