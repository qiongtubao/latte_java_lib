package latte.lib.db.impls;

import latte.lib.db.SqlDao;
import latte.lib.db.SqlImpl;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import javax.sql.DataSource;
import java.sql.SQLException;

public class SpringJdbcImpl implements SqlImpl {
    private JdbcTemplate template;
    public SpringJdbcImpl(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    @Override
    public void execSQL(String sql, Object... params) {
        template.update(sql, params[0], params[1], params[2], params[3]);
    }

    @Override
    public <T> T execSQLToObject(String sql, Object[] params, Class<T> t) {
        return template.queryForObject(sql, params, new BeanPropertyRowMapper<T>(t));
    }
}
