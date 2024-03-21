package latte.lib.common.utils;

import com.google.protobuf.ByteString;
import org.junit.Test;

import java.util.Arrays;

public class StringUtilsTest {
    protected static final ByteString RAW_DEFAULT_END =
            ByteString.copyFrom(new byte[] {'r', 0, 0, 1});

    public byte[] hexString2Byte(String hexString) {
        byte[] byteArray = new byte[hexString.length() / 2];

        for (int i = 0; i < byteArray.length; i++) {
            int index = i * 2;
            int intValue = Integer.parseInt(hexString.substring(index, index + 2), 16);
            byteArray[i] = (byte) intValue;
        }

        System.out.println(Arrays.toString(byteArray));
        return byteArray;
    }
    @Test
    public void ByteString() {
        System.out.println(RAW_DEFAULT_END);
        String hexString = "7200000100000000FB";
        ByteString end = ByteString.copyFrom(hexString2Byte(hexString));
        end = ByteString.copyFrom("r\000\000\001\000\000\000\000\373".getBytes());
        System.out.println(end);
        if (ByteString.unsignedLexicographicalComparator().compare(end, RAW_DEFAULT_END) >= 0) {
            System.out.println("aaaaa");
        }
    }
}
