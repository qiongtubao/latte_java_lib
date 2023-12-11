package latte.lib.tikv.api;


import jdk.internal.util.xml.impl.Pair;
import org.tikv.shade.com.google.protobuf.ByteString;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface StringCommand {
    /* 速度快  查询最新的数据  version = -1 */
    String get(String key);

    void set(String key, String value);
    /* 使用同一个version */
    List<String> mget(String... keys);

    void mset(String... keyorvalue);

}
