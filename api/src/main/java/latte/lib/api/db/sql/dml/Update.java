package latte.lib.api.db.sql.dml;
public interface Update<T> {
    String update(T o, T n) throws Exception;
}
