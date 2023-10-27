package latte.lib.db.impls;

import latte.lib.db.SqlImpl;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class SpringJdbcImpl implements SqlImpl {
    private JdbcTemplate template;
    public SpringJdbcImpl(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    @Override
    public void execSQL(String sql, Object... params) {
        template.update(sql, params);
    }


    @Override
    public <T> List<T> execSQLToObject(String sql, Object[] params, Class<T> t) {
        return template.query(sql, params, new BeanPropertyRowMapper<T>(t));
    }
}
