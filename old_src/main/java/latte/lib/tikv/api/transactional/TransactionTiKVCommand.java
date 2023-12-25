package latte.lib.tikv.api.transactional;

import org.tikv.shade.com.google.protobuf.ByteString;

public interface TransactionTiKVCommand {
    void putEvent(TransactionEvent.TransactionEventType type, ByteString key, ByteString value);
//    TransactionEvent getEvent(ByteString key);

    /**
     *  先查本地缓存 是否有更新数据的event  有的话先返回本地数据   如果没有再通过版本号去查询数据
     * @param key
     * @return
     */
    ByteString getOrLoadValue(ByteString key);

}
