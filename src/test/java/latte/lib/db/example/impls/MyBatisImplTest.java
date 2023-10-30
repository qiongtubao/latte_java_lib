package latte.lib.db.example.impls;

import latte.lib.db.example.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MyBatisImplTest {

    
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
