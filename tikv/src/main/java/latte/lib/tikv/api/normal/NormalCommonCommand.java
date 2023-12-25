package latte.lib.tikv.api.normal;


import latte.lib.tikv.api.CommonCommand;
import latte.lib.tikv.api.StoreType;
import org.tikv.shade.com.google.protobuf.ByteString;

import java.util.Iterator;

public interface NormalCommonCommand extends CommonCommand, TiKVCommand {
    @Override
    default void del(StoreType type, String key) {
        ByteString start = type.encodeKey(key);
        type.encodeKey(key);
        // 将 ByteString 转换为字节数组
        byte[] bytes = start.toByteArray();

        // 修改数组中最后一个字节的值
        bytes[bytes.length - 1]++;
        // 将修改后的字节数组转换回 ByteString
        ByteString end = ByteString.copyFrom(bytes);
        deleteRange(start,end, getCurrentVersion());
    }

    @Override
    default Iterator<String> scan(StoreType type, String start, String end) {
        return null;
    }
}
