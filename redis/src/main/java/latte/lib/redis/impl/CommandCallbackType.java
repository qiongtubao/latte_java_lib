package latte.lib.redis.impl;

import java.util.HashMap;
import java.util.Map;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.Client;
import redis.clients.util.Slowlog;

//jedis 2.x 使用
public enum CommandCallbackType {
  String {
    @Override
    public Object reply(Client client) {
      return client.getBulkReply();
    }
  },
  Byte {
    @Override
    public Object reply(Client client) {
      return client.getBinaryBulkReply();
    }
  },
  Long {
    @Override
    public Object reply(Client client) {
      return client.getIntegerReply();
    }
  },
  Status {
    @Override
    public Object reply(Client client) {
      return client.getStatusCodeReply();
    }
  },
  StringList {
    @Override
    public Object reply(Client client) {
      return client.getMultiBulkReply();
    }
  },
  LongList {
    @Override
    public Object reply(Client client) {
      return client.getIntegerMultiBulkReply();
    }
  },
  ObjectList {
    @Override
    public Object reply(Client client) {
      return client.getObjectMultiBulkReply();
    }
  },
  ByteList {
    @Override
    public Object reply(Client client) {
      return client.getBinaryMultiBulkReply();
    }
  },
  SlowLogList {
    @Override
    public Object reply(Client client) {
//      return null;
      return Slowlog.from(client.getObjectMultiBulkReply());
    }
  },
  StringMap {
    @Override
    public Object reply(Client client) {
      return BuilderFactory.STRING_MAP.build(client.getBinaryMultiBulkReply());
    }
  },
  ByteMap {
    @Override
    public Object reply(Client client) {
      return BuilderFactory.BYTE_ARRAY_MAP.build(client.getBinaryMultiBulkReply());
    }
  },
  Unknow {
    @Override
    public Object reply(Client client) {
      throw new RuntimeException("command unknow");
    }
  };



  public abstract Object reply(Client client);
  public static Map<String, CommandCallbackType> commandCallbackTable;
  static {
    initCommandCallbackTable();
  }
  private static void initCommandCallbackTable() {
    commandCallbackTable = new HashMap<>();
    commandCallbackTable.put("get".toLowerCase(), CommandCallbackType.Byte);
    commandCallbackTable.put("mget".toLowerCase(), CommandCallbackType.ByteList);
    commandCallbackTable.put("hget".toLowerCase(), CommandCallbackType.Byte);
    commandCallbackTable.put("hmget".toLowerCase(), CommandCallbackType.ByteList);
    commandCallbackTable.put("hgetall".toLowerCase(), CommandCallbackType.ByteMap);
    commandCallbackTable.put("info".toLowerCase(), CommandCallbackType.String);
    commandCallbackTable.put("set".toLowerCase(), CommandCallbackType.Status);
    commandCallbackTable.put("setex".toLowerCase(), CommandCallbackType.Status);
    commandCallbackTable.put("hset".toLowerCase(), CommandCallbackType.Status);
    commandCallbackTable.put("hmset".toLowerCase(), CommandCallbackType.Status);
    commandCallbackTable.put("mset".toLowerCase(), CommandCallbackType.Status);
    commandCallbackTable.put("del".toLowerCase(), CommandCallbackType.Long);
    commandCallbackTable.put("hdel".toLowerCase(), CommandCallbackType.Long);
  }
}
