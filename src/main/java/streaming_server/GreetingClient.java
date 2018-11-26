package streaming_server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.exercise.serverstreaming.GreetManyTimesRequest;
import io.grpc.exercise.serverstreaming.GreetServiceGrpc;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("This is a gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext(true)
                .build();

        // create a service client
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // unary

        // server streaming
        // prepare the request
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setFirstName("Shengwen")
                .build();
        // stream the responses  (in a blocking manner)
        greetClient.greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                });

        // do something
        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
