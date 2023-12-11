package latte.lib.tikv.api.normal;

import latte.lib.tikv.api.HashCommand;
import latte.lib.tikv.api.StoreType;
import latte.lib.tikv.api.encoder.HashEncoder;
import org.tikv.shade.com.google.protobuf.ByteString;

import java.util.*;
import java.util.stream.Collectors;

public interface NormalHashCommand extends TiKVCommand, HashCommand, HashEncoder {


    @Override
    default void hset(String key, String field, String value) {
        Map<ByteString, ByteString> map = new LinkedHashMap<>();
        map.put(encodeHashKeyFiled(key, field), encodeHashValue(value));
        benchPut(map, getCurrentVersion());
    }

    @Override
    default String hget(String key, String field) {
        List<ByteString> values= benchGet(Arrays.asList(encodeHashKeyFiled(key, field)), -1);
        return decodeHashValue(values.get(0));
    }

    @Override
    default void hmset(String key, String... fieldOrValue) {
        assert fieldOrValue.length % 2 != 0: "hmset param wrong";
        Map<ByteString, ByteString> map = new LinkedHashMap<>();
        for (int i = 0; i < fieldOrValue.length; i+=2) {
            map.put(encodeHashKeyFiled(key, fieldOrValue[i]), encodeHashValue(fieldOrValue[i+1]));
        }
        benchPut(map, getCurrentVersion());
    }

    @Override
    default List<String> hmget(String key, String... fields) {
        List<ByteString> rawkeys = Arrays.stream(fields).map(field -> {
            return encodeHashKeyFiled(key, field);
        }).collect(Collectors.toList());
        return benchGet(rawkeys, getCurrentVersion()).stream().map(rawValue-> {
            return decodeHashValue(rawValue);
        }).collect(Collectors.toList());
    }

    @Override
    default void hmdel(String key, String... fields) {
        List<ByteString> rawkeys = Arrays.stream(fields).map(field -> {
            return encodeHashKeyFiled(key, field);
        }).collect(Collectors.toList());
        benchDelete(rawkeys, getCurrentVersion());
    }

    @Override
    default Iterator<String> hscan(String key) {
        return null;
    }
}
