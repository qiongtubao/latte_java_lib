package latte.lib.tikv.api;

import java.util.Iterator;
import java.util.List;

public interface HashCommand {
    void hset(String key, String filed, String value);
    /* 为了速度 获得最新的数据*/
    String hget(String key, String filed);

    void hmdel(String key, String... field);
    /* 使用同一个版本*/
    Iterator<String> hscan(String key);

    void hmset(String key, String... filedorvalue);
    /* 使用同一个版本*/
    List<String> hmget(String key, String... filed);


}
