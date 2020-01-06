package ie.gmit.ds;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientSetup {

    private final ManagedChannel channel;
    private static final Logger logger = Logger.getLogger(ClientSetup.class.getName());
    private final GreeterGrpc.GreeterBlockingStub greeterClientStub;

    public ClientSetup(String[] args, int i) {

        this.channel = ManagedChannelBuilder.forAddress("Localhost", 8080)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();
        greeterClientStub = GreeterGrpc.newBlockingStub(channel);


    }

    public void greet(String name) {
        logger.info("Will try to greet " + name + " ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = greeterClientStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Greeting: " + response.getMessage());
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws Exception {
        ClientSetup client = new ClientSetup(new String[]{"localhost"}, 8080);
        try {
            client.greet("world");
        } finally {
            client.shutdown();
        }
    }
}
