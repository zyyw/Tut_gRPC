package exercise1;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.exercise.sum.SumRequest;
import io.grpc.exercise.sum.SumResponse;
import io.grpc.exercise.sum.SumServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class SumServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Welcome to iCalculator...");

        // plaintext server
        Server server = ServerBuilder.forPort(50051)
                .addService(new SumServiceImpl())
                .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        server.awaitTermination();
    }

    private static class SumServiceImpl extends SumServiceGrpc.SumServiceImplBase {
        public void getSum(SumRequest req, StreamObserver<SumResponse> responseObserver) {
            // get parameters sent through Request
            int a = req.getA();
            int b = req.getB();

            // create the response
            int result = a + b;
            SumResponse response = SumResponse.newBuilder().setResult(result).build();

            // send out response and close connection
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
