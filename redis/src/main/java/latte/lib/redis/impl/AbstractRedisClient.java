package latte.lib.redis.impl;

import latte.lib.redis.RedisClient;
import latte.lib.redis.api.normal.RedisCommonCommand;
import latte.lib.redis.api.normal.RedisStringCommand;

public abstract class AbstractRedisClient implements RedisClient, RedisStringCommand, RedisCommonCommand {

}
