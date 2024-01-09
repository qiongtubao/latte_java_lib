package latte.lib.redis;

import latte.lib.redis.api.CommonCommand;
import latte.lib.redis.api.HashCommand;
import latte.lib.redis.api.RedisCommand;
import latte.lib.redis.api.StringCommand;

public interface RedisClient extends RedisCommand, StringCommand, HashCommand, CommonCommand {

}
