package latte.lib.api.db.sql;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public interface SqlClient {
    boolean exec(String sql);
    List<Map<String,Object>> select(String sql);

    boolean supportInsertOrUpdate();

    void close() throws Exception ;
}
