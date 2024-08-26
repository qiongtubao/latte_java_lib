package latte.lib.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import latte.lib.grpc.impl.HelloServiceImpl;
import org.junit.Assert;
import org.junit.Test;

public class HelloGrpcClient {
  @Test
  public void send() throws Exception {
    ManagedChannel channel = Grpc.newChannelBuilder("127.0.0.1:10086", InsecureChannelCredentials.create())
        .build();
    LatteGrpcClient client = new LatteGrpcClient(channel);
    client.region("hello", HelloServiceImpl::new);
    String result = (String)client.send("hello", "world");
    Assert.assertEquals(result, "world");
  }


}
