package latte.lib.grpc;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.stub.AbstractBlockingStub;

public interface LatteGrpcSendHandler {
    Object send(Object... param) throws Exception;
}
