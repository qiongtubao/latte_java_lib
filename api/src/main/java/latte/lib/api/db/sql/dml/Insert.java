package latte.lib.api.db.sql.dml;

public interface Insert<T> {
    String insert(T o) throws Exception;
}
