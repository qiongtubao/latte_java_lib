package latte.lib.tikv.api.transactional;

import latte.lib.tikv.api.HashCommand;
import latte.lib.tikv.api.encoder.HashEncoder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public interface TransactionalHashCommand extends HashCommand, TransactionTiKVCommand, HashEncoder {


    @Override
    default void hset(String key, String filed, String value) {
        putEvent(TransactionEvent.TransactionEventType.PUT, encodeHashKeyFiled(key, filed), encodeHashValue(value));
    }

    @Override
    default String hget(String key, String filed) {
        return decodeHashValue(getOrLoadValue(encodeHashKeyFiled(key, filed)));
    }

    @Override
    default void hmset(String key, String... filedorvalue) {
        for(int i = 0;i < filedorvalue.length; i+=2){
            String filed = filedorvalue[i];
            String value = filedorvalue[i+1];
            putEvent(TransactionEvent.TransactionEventType.PUT, encodeHashKeyFiled(key, filed), encodeHashValue(value));
        }
    }

    @Override
    default List<String> hmget(String key, String... filed) {
        List<String> list = new LinkedList<>();
        for(int i = 0;i < filed.length; i++){
            list.add(decodeHashValue(getOrLoadValue(encodeHashKeyFiled(key, filed[i]))));
        }
        return list;
    }

    @Override
    default void hmdel(String key, String... filedorvalue) {
        for(int i = 0;i < filedorvalue.length; i+=2){
            String filed = filedorvalue[i];
            String value = filedorvalue[i+1];
            putEvent(TransactionEvent.TransactionEventType.DELETE, encodeHashKeyFiled(key, filed), encodeHashValue(value));
        }
    }

    @Override
    default Iterator<String> hscan(String key) {
        return null;
    }
}
