package ie.gmit.ds;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.logging.Logger;

public class ServerSetup {
    private Server server;
    private static final Logger logger = Logger.getLogger(ServerSetup.class.getName());


    public static void main(String[] args) throws IOException, InterruptedException {
        // Create Server
        final ServerSetup server = new ServerSetup();
        server.start();
        server.blockUntilShutdown();
    }


    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 8080;
        server = ServerBuilder.forPort(port)
                .addService(new PasswordServiceImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** Shutting down gRPC server since JVM is shutting down **");
                ServerSetup.this.stop();
                System.err.println("*** Server shut down ***");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}