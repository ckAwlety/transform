

import com.google.protobuf.ByteString;
import entity.Transform.ImageRequest;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.log4j.Log4j2;
import rpc.GreeterGrpc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
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
        File file = new File("C:\\Users\\ke.cao\\Downloads\\OneDrive_1_2022-9-22\\0526\\beads\\beads1.tif");
        BufferedImage image = ImageIO.read(file);
        int w = image.getWidth();
        int h = image.getHeight();
        int[] pixels = new int[w*h];
        for(int i=0;i<w;++i){
            for(int j=0;j<h;++j){
                pixels[i*h+j] = image.getRGB(i,j);
            }
        }
        byte[] b = new byte[4*w*h];

        int type = image.getType();
        int transparency = image.getColorModel().getTransparency();
        int pixelBits = image.getColorModel().getPixelSize();
        int numComponents = image.getColorModel().getNumComponents();
        boolean hasalpah = image.getColorModel().hasAlpha();
        boolean isbig;
        if (ByteOrder.nativeOrder()==ByteOrder.BIG_ENDIAN){
            isbig = true;
        }
        else{
            isbig = false;
        }
        int i=0;
        if(isbig){
            for(Integer x:pixels){
                b[i] = (byte) (x>>24&0xff);
                b[i+1] = (byte) (x>>16&0xff);
                b[i+2] = (byte) (x>>8&0xff);
                b[i+3] = (byte) (x&0xff);
                i += 4;
            }
        }
        else{
            for(Integer x:pixels){
                b[i+3] = (byte) (x>>24&0xff);
                b[i+2] = (byte) (x>>16&0xff);
                b[i+1] = (byte) (x>>8&0xff);
                b[i] = (byte) (x&0xff);
                i += 4;
            }
        }
        ClientWindow clientWindow = new ClientWindow("0.0.0.0",8080);
        ImageRequest imageRequest = ImageRequest.newBuilder().setImage(ByteString.copyFrom(b)).setHeight(h).setWidth(w).setType(type).setHasalpah(hasalpah).setTransparency(transparency).setNumComponents(numComponents).setPixelBits(pixelBits).setIsalphapre(isbig).build();
        entity.Transform.ImageReply imageReply = clientWindow.blockingStub.sayHello(imageRequest);
        log.info(imageReply.getMessage());
        clientWindow.shutdown();
    }

}
