package latte.lib.grpc.impl;

import io.grpc.Channel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import latte.lib.grpc.HelloReply;
import latte.lib.grpc.HelloRequest;
import latte.lib.grpc.HelloServiceGrpc;
import latte.lib.grpc.HelloServiceGrpc.HelloServiceBlockingStub;
import latte.lib.grpc.LatteGrpcSendHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceImpl  implements
    LatteGrpcSendHandler {

  private HelloServiceBlockingStub blockingStub;

  public HelloServiceImpl(Channel channel) {
    this.blockingStub = HelloServiceGrpc.newBlockingStub(channel);
  }
  static Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
  @Override
  public Object send(Object... param) throws Exception {
    String name = (String)param[0];
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    HelloReply response;

    try {
      response = blockingStub.sayHello(request);
      return response.getMessage();
    } catch (StatusRuntimeException e) {
      logger.warn("RPC failed: {0}", e.getStatus());
      throw e;

    }

  }

}
