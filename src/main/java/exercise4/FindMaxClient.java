package exercise4;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.exercise.findmax.FindMaxRequest;
import io.grpc.exercise.findmax.FindMaxResponse;
import io.grpc.exercise.findmax.FindMaxServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FindMaxClient {
    public static void main(String[] args) {
        System.out.println("This is a gRPC client");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext(true)
                .build();

        // create an asynchronous client
        FindMaxServiceGrpc.FindMaxServiceStub asyncClient = FindMaxServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<FindMaxRequest> requestObserver = asyncClient.findMax(new StreamObserver<FindMaxResponse>() {
            @Override
            public void onNext(FindMaxResponse findMaxResponse) {
                System.out.println("Response from server: " + findMaxResponse.getCurrentMax());
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending data");
                latch.countDown();
            }
        });

        Arrays.asList(1, 5, 3, 6, 2, 20).forEach(
                number -> {
                    System.out.println("Sending: " + number);
                    requestObserver.onNext(FindMaxRequest.newBuilder().setCurrentNumber(number).build());
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );

        requestObserver.onCompleted();

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
