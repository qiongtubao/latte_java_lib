package latte.lib.sql;

import java.util.List;
import java.util.Map;
import latte.lib.api.db.sql.SqlClient;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UserTest {
    protected abstract SqlClient getClient() throws Exception ;
    boolean printSql = true;
    static Logger logger = LoggerFactory.getLogger(UserTest.class);
    String createTableSql() {
        SqlParser parser = new SqlParser(User.class);
        String sql = parser.createTable();
        if (printSql) {
            logger.info("[sql] create table : {}", sql);
        }
        return sql;
    }

    public void createTable() throws Exception  {
        SqlClient client = getClient();
        client.exec(createTableSql());
    }

    public void insert() throws Exception {
        User user = new User();
        user.id = 1;
        user.age= 100;
        user.name = "zhangsan";
        SqlClient client = getClient();
        SqlParser parser = new SqlParser(User.class);
        String sql = parser.insert(user);
        if (printSql) {
            logger.info("[sql] insert sql :{}", sql);
        }
        client.exec(sql);
    }

    @Test
    public void query() throws Exception {
        SqlClient client = getClient();
        SqlParser parser = new SqlParser(User.class);
        User user = new User();
//        user.age= 100;
        user.name = "zhangsan";
        String sql = parser.select(user);
        if (printSql) {
            logger.info("[sql] insert sql :{}", sql);
        }
        List<Map<String, Object>> rs = client.select(sql);
        logger.info("{}", rs);




    }

    @Test
    public void update() throws Exception {
        SqlClient client = getClient();
        SqlParser parser = new SqlParser(User.class);
        User user = new User();
        user.age= 100;
        user.name = "zhangsan";

        User user1 = new User();
        user1.age = 200;
        String sql = parser.update(user, user1);
        if (printSql) {
            logger.info("[sql] update sql :{}", sql);
        }
        boolean result = client.exec(sql);
        logger.info("{}", result);

        //
        User q = new User();
//        user.age= 100;
        q.name = "zhangsan";
        String qSql = parser.select(q);
        List<Map<String, Object>> s = client.select(qSql);

        User u = (User) parser.from(s.get(0));
        logger.info("{}", u);


    }

    @Test
    public void delete() throws Exception {
        SqlClient client = getClient();
        SqlParser parser = new SqlParser(User.class);
        User user = new User();
        user.age= 200;
        user.name = "zhangsan";
        String sql = parser.delete(user);

        if (printSql) {
            logger.info("[sql] update sql :{}", sql);
        }
        boolean result = client.exec(sql);
        logger.info("{}", result);

    }
}
