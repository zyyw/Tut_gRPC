package exercise3;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.exercise.average.AverageRequest;
import io.grpc.exercise.average.AverageResponse;
import io.grpc.exercise.average.AverageServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AverageClient {
    public static void main(String[] args) {
        System.out.println("This is a gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext(true)
                .build();

        // create an asynchronous client
        AverageServiceGrpc.AverageServiceStub asyncAverageClient = AverageServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<AverageRequest> requestObserver = asyncAverageClient.getAverage(new StreamObserver<AverageResponse>() {
            @Override
            public void onNext(AverageResponse averageResponse) {
                // we get a response from the server
                System.out.println("Received a response from the server, result = " + averageResponse.getResult());
                // onNext() will be called only only
            }

            @Override
            public void onError(Throwable throwable) {
                // we get an error from the server
            }

            @Override
            public void onCompleted() {
                // the server is done sending us data
                // onComplete() will be called right after onNext()
                System.out.println("Server has completed sending us something");
                latch.countDown();
            }
        });

        // streaming message
        System.out.println("sending number: 1");
        requestObserver.onNext(AverageRequest.newBuilder().setNumber(1).build());
        System.out.println("sending number: 2");
        requestObserver.onNext(AverageRequest.newBuilder().setNumber(2).build());
        System.out.println("sending number: 3");
        requestObserver.onNext(AverageRequest.newBuilder().setNumber(3).build());
        System.out.println("sending number: 4");
        requestObserver.onNext(AverageRequest.newBuilder().setNumber(4).build());

        // we tell the server that the client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
