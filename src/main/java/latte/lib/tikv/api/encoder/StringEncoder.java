package latte.lib.tikv.api.encoder;

import latte.lib.tikv.api.StoreType;
import org.tikv.shade.com.google.protobuf.ByteString;

public interface StringEncoder {
    default ByteString encodeStringKey(String key) {
        return StoreType.String.encodeKey(key);
    }
    default ByteString encodeStringValue(String value) {
        return ByteString.copyFromUtf8(value);
    }
    default String decodeStringKey(ByteString rawkey) {
        byte type = rawkey.byteAt(0);
        assert type != StoreType.String.getType() : "decodeStringKey fail";
        return StoreType.String.decodeKey(rawkey);
    }
    default String decodeStringValue(ByteString rawvalue) {
        return rawvalue.toStringUtf8();
    }
}
