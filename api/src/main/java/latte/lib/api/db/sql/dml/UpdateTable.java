package latte.lib.api.db.sql.dml;
public interface UpdateTable<T> {
    String update(T o);
}
