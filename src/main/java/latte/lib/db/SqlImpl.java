package latte.lib.db;

import org.springframework.jdbc.core.PreparedStatementSetter;

import java.util.List;

public interface SqlImpl {
    void execSQL(String sql, Object... params);
    <T>  T execSQLToObject(String sql, Object[] params, Class<T> t)  throws Exception;

    default PreparedStatementSetter getSetter(Object... params) {
        PreparedStatementSetter setter = (preparedStatement) -> {
            for(int i = 0; i < params.length; i++ ) {
                if (params[i].getClass() == Long.class) {
                    preparedStatement.setLong(1, (long)params[i]);
                } else if (params[i].getClass() == Integer.class) {
                    preparedStatement.setInt(1, (int)params[i]);
                } else if (params[i].getClass() == String.class) {
                    preparedStatement.setString(1, (String)params[i]);
                } else {
                    throw new RuntimeException("non-support data type: "+ params[i].getClass().getName());
                }
            }
        };
        return setter;
    }
}
