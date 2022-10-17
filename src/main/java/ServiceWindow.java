import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.log4j.Log4j2;
import service.TransformServiceImpl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Log4j2

public class ServiceWindow {

    private final int port;
    private final Server server;
    public ServiceWindow(int port){
        this.port = port;
        this.server = ServerBuilder.forPort(port).addService(new TransformServiceImpl()).build();
    }

    public void start() throws IOException{
        server.start();
        log.info("Started server listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ServiceWindow.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            log.error("Server shut down.");
        }));
    }

    public void stop() throws InterruptedException{
        if(server != null){
            server.shutdown().awaitTermination(30,TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ServiceWindow server = new ServiceWindow(8080);
        server.start();
        server.blockUntilShutdown();
    }
}
