package latte.lib.api.db.sql.ddl;

public interface AlterTable<T> {
    String alter(T o);
}
