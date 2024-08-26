package latte.lib.grpc.impl;

import io.grpc.stub.StreamObserver;
import latte.lib.grpc.HelloReply;
import latte.lib.grpc.HelloRequest;
import latte.lib.grpc.HelloServiceGrpc.HelloServiceImplBase;

public class HelloServerHandler extends HelloServiceImplBase {
  @Override
  public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
    HelloReply reply = HelloReply.newBuilder().setMessage(req.getName()).build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }
}
