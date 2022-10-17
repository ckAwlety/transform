import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.internal.ClientStream;
import lombok.extern.log4j.Log4j2;
import entity.Homework.*;
import Grpc.GreeterGrpc.*;

import java.util.concurrent.TimeUnit;

import static Grpc.GreeterGrpc.newBlockingStub;

/**
 * @Author LynHB
 * @Description : g rpc 客户端入口类
 * @Date 19:56 2020/7/29
 **/
@Log4j2
public class ClientMain {

    private final GreeterBlockingStub blockingStub;
    private final ManagedChannel managedChannel;


    public ClientMain(String ip,int port) {
        this.managedChannel = ManagedChannelBuilder.forTarget(ip+":"+port).usePlaintext().build();
        this.blockingStub = newBlockingStub(this.managedChannel);
        log.info("Connected to server at "+ip+":"+port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.managedChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("Client shut down.");
        }));
    }

    private void shutdown() throws InterruptedException {
        this.managedChannel.shutdown().awaitTermination(5L, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        ClientMain clientMain = new ClientMain("0.0.0.0",8081);
        HelloReply helloReply = clientMain.blockingStub.sayHello(HelloRequest.newBuilder().setName(",曹可").build());
        log.info(helloReply.getMessage());
        clientMain.shutdown();

    }
}