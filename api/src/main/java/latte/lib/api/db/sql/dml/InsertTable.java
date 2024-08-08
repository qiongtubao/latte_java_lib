package latte.lib.api.db.sql.dml;

public interface InsertTable<T> {
    String insert(T o);
}
