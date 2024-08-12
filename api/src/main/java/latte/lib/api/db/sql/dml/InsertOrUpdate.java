package latte.lib.api.db.sql.dml;

public interface InsertOrUpdate<T> {
    String insertOrUpdate(T o) throws Exception;
}
