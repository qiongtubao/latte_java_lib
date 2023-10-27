package latte.lib.db.example.impls;


import com.google.inject.internal.util.Maps;
import latte.lib.db.example.User;
import latte.lib.db.example.UserDao;
import latte.lib.db.impls.SpringJdbcImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.List;
import java.util.Map;

public class SpringJdbcImplTest {
    UserDao dao;
    EmbeddedDatabase database;

    @Before
    public void init() {

        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        database = builder
                .setType(EmbeddedDatabaseType.H2)
                .addScript("user_tbl.sql")  // 可选：初始化数据库表结构的SQL脚本
                .build();
        dao = new UserDao();
        dao.setImpl(new SpringJdbcImpl(database));
    }
    
    @Test
    public void insert() throws Exception {
        
        
        User user = new User();
        user.setName("zgd");
        user.setAge(100);
        dao.insert(user);

        Map<String,Object> map = Maps.newHashMap();
        map.put("name","zgd");
        User user1 = dao.find(map, User.class);
        Assert.assertTrue(user1.getId() == 1);
        Assert.assertTrue(user1.getAge() == 100);
        user1.setAge(200);
        dao.update(user1);
        User user2 = dao.find(map, User.class);
        Assert.assertTrue(user2.getAge() == 200);

        dao.del(user2);

        User user3 = dao.find(map, User.class);
        Assert.assertEquals(user3, null);
    }

    @Test
    public void base() throws Exception {

            JdbcTemplate jdbcTemplate = new JdbcTemplate(database);
            User user = new User();
            user.setAge(100);
            user.setName("zgd");
            //name和age 无法使用？ 替代
            String sql = "INSERT INTO user_tbl (name, age) VALUES (?, ?)";
            jdbcTemplate.update(sql, user.getName(), user.getAge());
            sql = "SELECT * FROM user_tbl";
            List<User> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));

            Assert.assertTrue(users.size() == 1);
            Assert.assertEquals(users.get(0).getName(), "zgd");
            Assert.assertEquals(users.get(0).getAge(), 100);


    }
}
