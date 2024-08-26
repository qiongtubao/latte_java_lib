package latte.lib.grpc;

import io.grpc.BindableService;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatteGrpcServer {
  private static final Logger logger = LoggerFactory.getLogger(LatteGrpcServer.class.getName());

  private Server server;

  List<BindableService> services = new LinkedList<>();
  int port;

  public LatteGrpcServer addSerive(BindableService service) {
      services.add(service);
      return this;
  }
  public LatteGrpcServer start(int port) throws IOException {
    /* The port on which the server should run */
    io.grpc.ServerBuilder builder = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create());
    for (BindableService service : services) {
      builder = builder.addService(service);
    }
    server = builder.build().start();

    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          LatteGrpcServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
    return  this;
  }

  private void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  public void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
//  public static void main(String[] args) throws IOException, InterruptedException {
//    final LatteGrpcServer server = new LatteGrpcServer();
//    server.start();
//    server.blockUntilShutdown();
//  }

}
