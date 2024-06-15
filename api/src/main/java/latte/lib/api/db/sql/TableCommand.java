package latte.lib.api.db.sql;

import java.util.Map;

public interface TableCommand {
  Map<String, String> query(String sql);


}
