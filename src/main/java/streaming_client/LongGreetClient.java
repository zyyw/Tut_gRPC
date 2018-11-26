package streaming_client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.exercise.clientstreaming.LongGreetRequest;
import io.grpc.exercise.clientstreaming.LongGreetResponse;
import io.grpc.exercise.clientstreaming.LongGreetServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LongGreetClient {
    public static void main(String[] args) {
        System.out.println("This is a gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext(true)
                .build();

        // create an asynchronous client
        LongGreetServiceGrpc.LongGreetServiceStub asyncClient = LongGreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse longGreetResponse) {
                // we get a response from the server
                System.out.println("Received a response from the server");
                System.out.println(longGreetResponse.getResult());
                // onNext will be called only once
            }

            @Override
            public void onError(Throwable throwable) {
                // we get an error from the server
            }

            @Override
            public void onCompleted() {
                // the server is done sending us data
                // onCompleted will be called right after onNext()
                System.out.println("Server has completed sending us something");
                latch.countDown();
            }
        });

        // streaming message #1
        System.out.println("sending message 1");
        requestObserver.onNext(LongGreetRequest.newBuilder().setFirstName("Stephane").build());
        // streaming message #2
        System.out.println("sending message 2");
        requestObserver.onNext(LongGreetRequest.newBuilder().setFirstName("John").build());
        // streaming message #3
        System.out.println("sending message 3");
        requestObserver.onNext(LongGreetRequest.newBuilder().setFirstName("Mark").build());

        // we tell the server that the client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
