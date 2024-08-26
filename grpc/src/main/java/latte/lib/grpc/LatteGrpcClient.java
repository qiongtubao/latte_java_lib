package latte.lib.grpc;

import com.google.common.collect.Maps;
import io.grpc.Channel;
import io.grpc.stub.AbstractAsyncStub;
import io.grpc.stub.AbstractBlockingStub;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatteGrpcClient {
  private static final Logger logger = LoggerFactory.getLogger(LatteGrpcClient.class.getName());

  Map<String, LatteGrpcSendHandler> handler = Maps.newConcurrentMap();

  Channel channel;
  /** Construct client for accessing HelloWorld server using the existing channel. */
  public LatteGrpcClient(Channel channel) {
      this.channel = channel;
  }

  public LatteGrpcClient region(String handleName, Function<Channel, LatteGrpcSendHandler> newStub) {
    handler.put(handleName, newStub.apply(this.channel));
    return this;
  }

  public Object send(String handleName, Object... result) throws Exception {
    return handler.get(handleName).send(result);
  }

}
