package latte.lib.db.example.impls;


import latte.lib.db.example.User;
import latte.lib.db.example.UserDao;
import latte.lib.db.impls.SpringJdbcImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class SpringJdbcImplTest {
    UserDao dao;
    
    @Before
    public void init() {
        dao = new UserDao();
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        EmbeddedDatabase database = builder
                .setType(EmbeddedDatabaseType.H2)
                .addScript("user_tbl.sql")  // 可选：初始化数据库表结构的SQL脚本
                .build();
        dao.setImpl(new SpringJdbcImpl(database));
    }
    
    @Test
    public void insert() throws Exception {
        
        
        User user = new User();
        user.setName("zgd");
        user.setAge(100);
        dao.insert(user);     
        User user1 = dao.find(user);
        Assert.assertTrue(user1.getId() == 1);

    }
}
