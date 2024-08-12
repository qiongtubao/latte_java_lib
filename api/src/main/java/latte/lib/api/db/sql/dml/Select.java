package latte.lib.api.db.sql.dml;

public interface Select<T> {
    String select(T o) throws Exception;
}
