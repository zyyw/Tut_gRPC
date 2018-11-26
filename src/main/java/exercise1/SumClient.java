package exercise1;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.exercise.sum.SumRequest;
import io.grpc.exercise.sum.SumResponse;
import io.grpc.exercise.sum.SumServiceGrpc;

public class SumClient {
    private static final int a = 12;
    private static final int b = 34;
    public static void main(String[] args) {
        System.out.println("This is a gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext(true)
                .build();

        // create a service client (blocking - synchronous
        System.out.println("creating a stub");
        SumServiceGrpc.SumServiceBlockingStub sumClient = SumServiceGrpc.newBlockingStub(channel);

        // create a protocol buffer request message
        SumRequest request = SumRequest.newBuilder()
                .setA(a)
                .setB(b)
                .build();

        // call the RPC and get back the result of the sum of the 2 integers sent out as request params
        SumResponse response = sumClient.getSum(request);

        // print out the sum returned from server, at the client side console
        System.out.println("" + a + " + " + b + " = " + response.getResult());

        // shutdown the channel
        channel.shutdown();
    }
}
