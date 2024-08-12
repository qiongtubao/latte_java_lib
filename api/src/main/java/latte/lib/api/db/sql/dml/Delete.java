package latte.lib.api.db.sql.dml;

public interface Delete<T> {
    String delete(T o) throws Exception;
}
