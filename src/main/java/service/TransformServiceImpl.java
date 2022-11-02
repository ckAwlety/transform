package service;

import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;
import entity.Transform.*;
import io.grpc.stub.StreamObserver;
import rpc.GreeterGrpc;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TransformServiceImpl extends GreeterGrpc.GreeterImplBase{
    @Override
    public void sayHello(ImageRequest req, StreamObserver<ImageReply> responseObserver) throws IOException {
        int w = req.getWidth();
        int h = req.getHeight();
        byte[] pixels = req.getImage().toByteArray();
        int type = req.getType();
        int pixelBits = req.getPixelBits();
        int numComponents = req.getNumComponents();
        int transparency = req.getTransparency();
        boolean hasalpah = req.getHasalpah();
        boolean isbig = req.getIsalphapre();
        BufferedImage image = new BufferedImage(w, h, type);
        int index = 0;
        if(isbig){
            for(int i=0;i<pixels.length;i=i+4) {
                int temp = 0;
                for (int j = 0; j < 4; ++j) {
                    temp += (pixels[i + j] & 0xff) << ((3 - j) * 8);
                }
                int x = index / h;
                int y = index % h;
                index += 1;
                image.setRGB(x, y, temp);
            }
        }
        else{
            for(int i=0;i<pixels.length;i=i+4) {
                int temp = 0;
                for (int j = 0; j < 4; ++j) {
                    temp += (pixels[i + j] & 0xff) << (j * 8);
                }
                int x = index / h;
                int y = index % h;
                index += 1;
                image.setRGB(x, y, temp);
            }
        }



        System.out.println(image);
        ImageIO.write(image, "PNG", new FileOutputStream("C:\\Users\\ke.cao\\Desktop\\c.tif"));
        ImageReply reply = ImageReply.newBuilder().setMessage("图片已收到 宽："+ w + "高：" + h).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
