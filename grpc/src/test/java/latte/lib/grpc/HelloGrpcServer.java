package latte.lib.grpc;

import java.io.IOException;
import latte.lib.grpc.impl.HelloServerHandler;
import latte.lib.grpc.impl.HelloServiceImpl;
import org.junit.Test;

public class HelloGrpcServer {

    @Test
    public void start_server() throws Exception {
        LatteGrpcServer server = new LatteGrpcServer();
        server.addSerive(new HelloServerHandler())
            .start(10086)
            .blockUntilShutdown();
    }

}
