package latte.lib.kv.redis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisClientInfo {
  String host;
  int port;
  int timeout;
  int maxIdle;
  int maxTotal;
  String uri = "";
//  int db = 0;
//  String password;
}
