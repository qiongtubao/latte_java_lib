package latte.lib.api.db.sql.ddl;

public interface DropTable<T> {
    String delete(T o);
}
