package latte.lib.db.example;

import latte.lib.db.SqlDao;

import java.util.Arrays;
import java.util.List;

public class UserDao extends SqlDao<User> {

    @Override
    protected String getTableName() {
        return "user_tbl";
    }

    @Override
    protected List<String> getKey() {
        return Arrays.asList("id");
    }
}
