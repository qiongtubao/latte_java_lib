package latte.lib.tikv.api;

import org.tikv.shade.com.google.protobuf.ByteString;
public enum StoreType {

    String {
        @Override
        public ByteString encodeKey(java.lang.String key) {
            return ByteString.copyFrom(new byte[]{getType()}).concat(ByteString.copyFromUtf8(key));
        }

        @Override
        public java.lang.String decodeKey(ByteString rawKey) {
            return rawKey.substring(1).toStringUtf8();
        }

        @Override
        public byte getType() {
            return 0;
        }
    },
    Hash {
        @Override
        public ByteString encodeKey(java.lang.String key) {
            return null;
        }

        @Override
        public java.lang.String decodeKey(ByteString rawKey) {
            return null;
        }

        @Override
        public byte getType() {
            return 1;
        }
    };
    public abstract ByteString encodeKey(String key);
    public abstract String decodeKey(ByteString rawKey);

    public abstract byte getType();

//    public abstract ByteString encodeValue(String value);
//    public abstract String decodeValue(ByteString rawValue);

}
