package latte.lib.redis.api;

import latte.lib.redis.RedisType;

import java.util.LinkedHashMap;
import java.util.Map;

public interface CommonCommand {
     public static class ActionTypeValue {
        String value;
        public ActionTypeValue(String value) {
            this.value = value;
        }

        public String getString() {
            return value;
        }

        public double getDouble() {
            return Double.parseDouble(this.value);
        }

         public long getLong() {
            return Long.parseLong(this.value);
        }

        public  Map<String, ActionTypeValue> getMap() {
            System.out.println("value: " + value);
            Map<String, ActionTypeValue> result = new LinkedHashMap<>();
            String[] kvs = value.split(",");
            for(int i = 0; i < kvs.length; i++) {
                String[] kv = kvs[i].split("=");
                if (kv.length != 2) {
                    throw new RuntimeException("get map fail " + kvs[i]);
                }
//                System.out.println("getMap key:" + kv[0] + "=" + kv[1]);
                result.put(kv[0], new ActionTypeValue(kv[1]));
            }
            return result;
        }

         @Override
         public String toString() {
             return "ActionTypeValue{" +
                     "value='" + value + '\'' +
                     '}';
         }
     }
    enum ActionType {
        Keyspace,
        Replication,
        Stats,
        Cpu;

    };



    default Map<String, ActionTypeValue> decodeInfo(String str) {
        Map<String, ActionTypeValue> result = new LinkedHashMap<>();
        String[] splits = str.split("\r\n");
        for(int i = 0; i < splits.length; i++) {
            if (splits[i].length() == 0 || splits[i].charAt(0) == '#') {
                continue;
            }
            String[] r = splits[i].split(":");
            if(r.length <= 1) {
                continue;
            } else if (r.length > 2) {
                throw  new RuntimeException("info decode" + ":" + str);
            }
//            System.out.println("put " + r[0] + "= " + r[1]);
            result.put(r[0],
                    new ActionTypeValue(r[1]));
        }
        return result;
    }

    void del(String key);
    String infoString(RedisType rt, ActionType type);
    Map<String,ActionTypeValue> info(RedisType rt, ActionType type);

    ActionTypeValue info(RedisType rt, ActionType type, String properties);
    Map<String,ActionTypeValue> infoAll(RedisType rt);

}
