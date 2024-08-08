package latte.lib.api.db.sql.ddl;

public interface CreateTable<T> {
    String create(T o);
}
