package latte.lib.tikv.api.normal;

import latte.lib.tikv.api.StringCommand;
import latte.lib.tikv.api.encoder.StringEncoder;
import org.tikv.shade.com.google.protobuf.ByteString;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface NormalStringCommand extends TiKVCommand, StringCommand, StringEncoder {



    /**
     *  get version is -1(last)  but mget version is currentVersion
     * @param key
     * @return
     */
    @Override
    default String get(String key) {
        List<ByteString> values= benchGet(Arrays.asList(encodeStringKey(key)), -1);
        return decodeStringValue(values.get(0));
    }

    @Override
    default void set(String key, String value) {
        Map<ByteString, ByteString> map = new LinkedHashMap<>();
        map.put(encodeStringKey(key), encodeStringValue(value));
        benchPut(map, getCurrentVersion());
    }
    /**
     *  mget version is currentVersion
     * @param key
     * @return
     */
    @Override
    default List<String> mget(String... keys) {
        List<ByteString> rawValues= benchGet(Arrays.stream(keys).map(key -> {
            return encodeStringKey(key);
        }).collect(Collectors.toList()), getCurrentVersion());
        return rawValues.stream().map(rawvalue -> {
            return decodeStringValue(rawvalue);
        }).collect(Collectors.toList());
    }

    @Override
    default void mset(String... keyOrValue) {
        assert keyOrValue.length != 0 : "mset param wrong";
        Map<ByteString, ByteString> map = new LinkedHashMap<>();
        for (int i = 0; i < keyOrValue.length; i+=2) {
            String key = keyOrValue[i];
            String value = keyOrValue[i + 1];
            map.put(encodeStringKey(key), encodeStringValue(value));
        }
        benchPut(map, getCurrentVersion());
    }

}
