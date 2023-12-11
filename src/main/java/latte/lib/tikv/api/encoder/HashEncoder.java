package latte.lib.tikv.api.encoder;

import latte.lib.tikv.api.StoreType;
import org.tikv.shade.com.google.protobuf.ByteString;

public interface HashEncoder {
    default ByteString encodeHashKeyFiled(String key, String field) {
        return StoreType.Hash.encodeKey(key).concat(ByteString.copyFromUtf8(field));
    }

    default ByteString encodeHashValue(String value) {
        return ByteString.copyFromUtf8(value);
    }

    default String decodeHashValue(ByteString rawValue) {
        return rawValue.toStringUtf8();
    }
}
