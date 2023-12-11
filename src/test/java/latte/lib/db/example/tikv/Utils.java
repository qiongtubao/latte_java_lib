package latte.lib.db.example.tikv;

import lombok.Getter;
import lombok.Setter;

public class Utils {
    public static String randomKey(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append((char) ('a' + (int) (26 * Math.random())));
        }
        return sb.toString();
    }

    @Getter
    @Setter
    static public class KV {
        String key;
        String value;
        public KV(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
