package latte.lib.kv.redis.impl.vertx;


import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisOptions;
import io.vertx.redis.client.Request;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import latte.lib.kv.redis.RedisClientInfo;
import latte.lib.kv.redis.impl.AbstractExecKVClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultVertxRedisClient extends AbstractExecKVClient {

  static Logger logger = LoggerFactory.getLogger(DefaultVertxRedisClient.class);

  Redis client;
  public DefaultVertxRedisClient(RedisClientInfo info)
      throws ExecutionException, InterruptedException {
    VertxOptions options = new VertxOptions();
    if (info.getUri().indexOf("unix") != -1) {
      options= options.setPreferNativeTransport(true);
    }
    Vertx vertx = Vertx.vertx(options);
    RedisOptions ro = (new RedisOptions()).setConnectionString(info.getUri());
    ro.setMaxPoolSize(info.getMaxTotal());
    ro.setMaxPoolWaiting(info.getMaxIdle());
    client = Redis.createClient(vertx, ro);
//    try {
      connect();
//    } catch (Exception e) {
//      logger.error("{} vertx  connect fail {}", info.getUri(), e);
//    }
  }
  @Override
  public void close() throws Exception {
    client.close();
  }

  public boolean connect() throws ExecutionException, InterruptedException {
    CompletableFuture<Boolean> future = new CompletableFuture<>();
    client.connect().onSuccess(conn-> {
      future.complete(true);
    }).onFailure(ex -> {
      future.completeExceptionally(ex);
    });
    return future.get();
  }
  @Override
  protected Object execCommand(String... args) throws ExecutionException, InterruptedException {
    CompletableFuture<Object> future = new CompletableFuture<>();
    VecrtxCommandCallbackType type = VecrtxCommandCallbackType.commandCallbackTable.get(args[0].toLowerCase());
    Request req = type.get();
    for(int i = 1; i < args.length; i++) {
      req = req.arg(args[i]);;
    }
    client.send(req, ar -> {
      if (ar.succeeded()) {
        future.complete(type.reply(ar.result()));
      } else {
        future.completeExceptionally(ar.cause());
      }
    });
    return future.get();
  }
}
