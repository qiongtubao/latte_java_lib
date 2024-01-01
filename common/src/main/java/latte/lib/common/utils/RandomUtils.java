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
}
