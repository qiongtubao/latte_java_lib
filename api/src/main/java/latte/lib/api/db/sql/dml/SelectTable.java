package latte.lib.api.db.sql.dml;

public interface SelectTable<T> {
    String select(T o);
}
