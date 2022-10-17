package service;


import com.google.protobuf.ByteString;
import entity.Transform.*;
import rpc.GreeterGrpc;
import io.grpc.stub.StreamObserver;

public class TransformServiceImpl extends GreeterGrpc.GreeterImplBase{
    @Override
    public void sayHello(ImageRequest req, StreamObserver<ImageReply> responseObserver) {
        int w = req.getWidth();
        int h = req.getHeight();
        byte[] pixels = req.getImage().toByteArray();
        ImageReply reply = ImageReply.newBuilder().setMessage("图片已收到 宽："+ w + "高：" + h +"图片像素：" + pixels).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
