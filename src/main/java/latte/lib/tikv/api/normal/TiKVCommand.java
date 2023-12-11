package latte.lib.tikv.api.normal;

import org.tikv.shade.com.google.protobuf.ByteString;

import java.util.List;
import java.util.Map;

public interface TiKVCommand {

    long getCurrentVersion();

    default long getLastVersion() {
        return -1;
    }
    void benchPut(Map<ByteString, ByteString> keyValues, long version);
    List<ByteString> benchGet(List<ByteString> keys, long version);

    void benchDelete(List<ByteString> keys, long version);
    void deleteRange(ByteString start, ByteString end, long version);
}
