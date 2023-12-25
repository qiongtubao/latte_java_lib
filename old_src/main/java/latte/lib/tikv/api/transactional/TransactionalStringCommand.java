package latte.lib.tikv.api.transactional;

import latte.lib.tikv.api.StringCommand;
import latte.lib.tikv.api.encoder.StringEncoder;
import org.tikv.shade.com.google.protobuf.ByteString;

import java.util.List;

public interface TransactionalStringCommand extends StringCommand, TransactionTiKVCommand, StringEncoder {
    @Override
    default void set(String key, String value) {
        putEvent(TransactionEvent.TransactionEventType.PUT, encodeStringKey(key), encodeStringValue(value));
    }

    @Override
    default String get(String key) {
        ByteString result =  getOrLoadValue(encodeStringKey(key));
        return decodeStringValue(result);
    }

    /**
     *  应该可以做成批量查询  先过滤出本地有的数据 然后再批量
     * @param key
     * @return
     */
    @Override
    default List<String> mget(String... key) {
        return null;
    }

    @Override
    default void mset(String... keyOrValue) {
        assert keyOrValue.length%2 !=0: "mset param wrong";
        for(int i = 0;i < keyOrValue.length; i+=2 ) {
            putEvent(TransactionEvent.TransactionEventType.PUT, encodeStringKey(keyOrValue[i]), encodeStringValue(keyOrValue[i+1]));
        }
    }
}
