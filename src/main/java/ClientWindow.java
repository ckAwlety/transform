import com.google.protobuf.ByteString;
import entity.Transform;
import rpc.GreeterGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.log4j.Log4j2;
import entity.Transform.*;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;


@Log4j2
public class ClientWindow {

    private final GreeterGrpc.GreeterBlockingStub blockingStub;
    private final ManagedChannel managedChannel;

    public ClientWindow(String ip,int port) {
        this.managedChannel = ManagedChannelBuilder.forTarget(ip+":"+port).usePlaintext().build();
        this.blockingStub = GreeterGrpc.newBlockingStub(this.managedChannel);
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

    public static void main(String[] args) throws InterruptedException, IOException {
        File file = new File("C:\\Users\\ke.cao\\Desktop\\a.png");
        BufferedImage image = ImageIO.read(file);
        int w = image.getWidth();
        int h = image.getHeight();
        byte[] pixels = Files.readAllBytes(file.toPath());
        ClientWindow clientWindow = new ClientWindow("0.0.0.0",8080);
        ImageRequest imageRequest = ImageRequest.newBuilder().setImage(ByteString.copyFrom(pixels)).setHeight(h).setWidth(w).build();
        Transform.ImageReply imageReply = clientWindow.blockingStub.sayHello(imageRequest);
        log.info(imageReply.getMessage());
        clientWindow.shutdown();
    }

}
