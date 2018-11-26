package exercise2;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.exercise.decomposition.DecompositionRequest;
import io.grpc.exercise.decomposition.DecompositionServiceGrpc;

public class DecompositionClient {
    public static void main(String[] args) {
        System.out.println("This is a gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext(true)
                .build();

        // create a service client
        DecompositionServiceGrpc.DecompositionServiceBlockingStub decompositionClient = DecompositionServiceGrpc.newBlockingStub(channel);

        // prepare the request
        DecompositionRequest decompositionRequest = DecompositionRequest.newBuilder().setNumber(120).build();

        System.out.print("120 = 1");
        // stream the response (in a blocking manner)
        decompositionClient.getDecomposition(decompositionRequest).forEachRemaining(
                decompositionResponse -> {
                    System.out.print(" * " + decompositionResponse.getPrime());
                }
        );
        System.out.println();

        // do something
        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
