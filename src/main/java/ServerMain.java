
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.log4j.Log4j2;
import service.GreeterServiceImpl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author LynHB
 * @Description :g rpc server入口类
 * @Date 19:55 2020/7/29
 **/
@Log4j2
public class ServerMain {
    private final int port;
    private final Server server;

    public ServerMain(int port){
        this.port = port;
        /*
         * 创建Server
         * addService()添加对应的服务
         * forPort()指定端口
         */
        this.server = ServerBuilder.forPort(port).addService(new GreeterServiceImpl()).build();
    }


    public void start() throws IOException{
        server.start();
        log.info("Started server listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ServerMain.this.stop();
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
        ServerMain server = new ServerMain(8081);
        server.start();
        server.blockUntilShutdown();
    }
}
