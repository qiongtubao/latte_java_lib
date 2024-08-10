package latte.lib.common.utils;

import java.util.Random;

public class RandomUtils {
    static Random random = new Random();
    static public byte[] randomBytes(int size) {
        byte[] result = new byte[size];
        random.nextBytes(result);
        return result;
    }

    static public String randomString(int size) {
        return new String(randomBytes(size));
    }

    static public String randomAlphabeticString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + (int) (26 * Math.random())));
        }
        return sb.toString();
    }
}
