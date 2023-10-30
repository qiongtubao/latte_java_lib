package latte.lib.db.impls;

import latte.lib.db.SqlImpl;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class MyBatisImpl implements SqlImpl {
    SqlSession sqlSession;
    public MyBatisImpl(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }


    @Override
    public void execSQL(String sql, Object... params) {

    }

    @Override
    public <T> List<T> execSQLToObject(String sql, Object[] params, Class<T> t) throws Exception {
        return null;
    }
}
