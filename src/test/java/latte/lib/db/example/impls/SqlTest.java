package latte.lib.db.example.impls;

import com.google.inject.internal.util.Maps;
import latte.lib.db.SqlImpl;
import latte.lib.db.example.User;
import latte.lib.db.example.UserDao;
import latte.lib.db.impls.MyBatisImpl;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public abstract class SqlTest {
    UserDao dao;

    abstract SqlImpl createSqlImpl() throws IOException;


    @Before
    public void init() throws IOException {

        dao = new UserDao();
        dao.setImpl(createSqlImpl());
    }
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
}
