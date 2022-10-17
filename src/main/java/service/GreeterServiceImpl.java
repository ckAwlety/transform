package service;

import Grpc.GreeterGrpc;
import entity.Homework.*;
import Grpc.GreeterGrpc.*;
import io.grpc.stub.StreamObserver;

public class GreeterServiceImpl extends GreeterGrpc.GreeterImplBase {
    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver){
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello" + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
