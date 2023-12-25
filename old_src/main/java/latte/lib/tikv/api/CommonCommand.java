package latte.lib.tikv.api;

import java.util.Iterator;

public interface CommonCommand {
    /* 默认是使用同一个版本*/
    Iterator<String> scan(StoreType type, String start, String end);

    void del(StoreType type, String key);
}
