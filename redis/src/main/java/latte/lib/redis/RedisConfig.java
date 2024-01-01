package latte.lib.redis;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RedisConfig {

    int readTimeout;
    int connectedTimeout;
    int poolMaxTotal;
    int poolIdle;
}
