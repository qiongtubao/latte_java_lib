package latte.lib.db.example.impls;

import com.google.inject.internal.util.Maps;
import latte.lib.db.SqlImpl;
import latte.lib.db.example.User;
import latte.lib.db.example.UserDao;
import latte.lib.db.impls.MyBatisImpl;
import latte.lib.db.impls.SpringJdbcImpl;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 *  目前还无法对融入原来自动化。我居然还觉得mybatis还挺好用的
 */
public class MyBatisImplTest extends SqlTest {
    UserDao dao;
    EmbeddedDatabase database;

    @Override
    SqlImpl createSqlImpl() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis_config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 创建 SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return new MyBatisImpl(sqlSession);
    }
    @Test
    public void insert() throws Exception {
        super.insert();
    }
    @Test
    public void base() throws IOException {
        // 加载 MyBatis 配置文件
        InputStream inputStream = Resources.getResourceAsStream("mybatis_config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

// 创建 SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();

// 获取映射器
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        userMapper.createUserTable();
        User user = new User();
        user.setAge(100);
        user.setName("zgd");
        userMapper.insertEntity(user);

// 调用映射器方法
        List<User> entities = userMapper.getAllEntities();

// 处理结果
        for (User entity : entities) {
            System.out.println(entity);
        }

// 关闭 SqlSession
        sqlSession.close();
    }
}
