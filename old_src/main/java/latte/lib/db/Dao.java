package latte.lib.db;

import java.util.List;
import java.util.Map;

public interface Dao<T> {
    void insert(T t) throws Exception;
    void update(T t) throws Exception;
    
    T find(T t) throws Exception;
    
    void del(T t) throws Exception;
    
    
}
