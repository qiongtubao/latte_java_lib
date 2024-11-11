package latte.lib.kv.redis.impl.vertx;

import io.vertx.redis.client.Command;
import io.vertx.redis.client.Request;
import io.vertx.redis.client.Response;
import java.util.HashMap;
import java.util.Map;
import latte.lib.kv.redis.impl.jedis2x.CommandCallbackType;


public enum VecrtxCommandCallbackType {
  GET {
    @Override
    public Object reply(Response resp) {
      if (resp == null) return null;
      return resp.toBytes();
    }

    @Override
    public Request get() {
      return Request.cmd(Command.GET);
    }
  },
  SET {
    @Override
    public Object reply(Response resp) {
      return resp.toString();
    }

    @Override
    public Request get() {
      return Request.cmd(Command.SET);
    }
  };
  public abstract Object reply(Response resp);
  public abstract Request get();

  public static Map<String, VecrtxCommandCallbackType> commandCallbackTable;
  static {
    initCommandCallbackTable();
  }
  static void initCommandCallbackTable() {
    commandCallbackTable = new HashMap<>();
    commandCallbackTable.put("set".toLowerCase(), VecrtxCommandCallbackType.SET);
    commandCallbackTable.put("get".toLowerCase(), VecrtxCommandCallbackType.GET);
  }

}
