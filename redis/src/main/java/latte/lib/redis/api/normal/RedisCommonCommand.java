package latte.lib.redis.api.normal;

import latte.lib.redis.RedisType;
import latte.lib.redis.api.CommonCommand;
import latte.lib.redis.api.RedisCommand;
import latte.lib.redis.api.StringCommand;

import java.util.Map;

public interface RedisCommonCommand extends CommonCommand, RedisCommand {
    default void del(String key) {
        execCommand("del", key);
    }

    default String infoString(RedisType rt, ActionType type) {
        return parseString(execCommand(rt.equals(RedisType.CRDT) ? "crdt.info" : "info", type.name()));
    }
    default Map<String,ActionTypeValue> info(RedisType rt, ActionType type) {
        String str = infoString(rt, type);
        return decodeInfo(str);
    }

    default ActionTypeValue info(RedisType rt, ActionType type, String properties) {
        String str = infoString(rt, type);
        //TODO match key
        return decodeInfo(str).get(properties);
    }
    default Map<String,ActionTypeValue> infoAll(RedisType rt) {
        return info(rt, null);
    }
}
