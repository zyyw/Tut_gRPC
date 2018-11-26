package streaming_BiDi;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.exercise.BiDiStreaming.GreetEveryoneRequest;
import io.grpc.exercise.BiDiStreaming.GreetEveryoneResponse;
import io.grpc.exercise.BiDiStreaming.GreetEveryoneServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetEveryoneClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("This is a gRPC client");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext(true)
                .build();

        // create an asynchronous client
        GreetEveryoneServiceGrpc.GreetEveryoneServiceStub asyncClient = GreetEveryoneServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestObserver = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse greetEveryoneResponse) {
                System.out.println("Response from server: " + greetEveryoneResponse.getResult());
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

        Arrays.asList("Stephane", "John", "Mark", "Patricia").forEach(
                name -> {
                    System.out.println("Sending: " + name);
                    requestObserver.onNext(GreetEveryoneRequest.newBuilder().setFirstName(name).build());
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
