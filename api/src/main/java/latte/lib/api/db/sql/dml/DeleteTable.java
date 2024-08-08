package latte.lib.api.db.sql.dml;

public interface DeleteTable<T> {
    String delete(T o);
}
