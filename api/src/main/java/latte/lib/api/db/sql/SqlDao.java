package latte.lib.api.db.sql;

import java.util.List;

public interface SqlDao<T> {
    List<T> findAll(T o);
    void insert(T o);
    void update(T o);
    T findOne(T o);

    void del(T o);
}
