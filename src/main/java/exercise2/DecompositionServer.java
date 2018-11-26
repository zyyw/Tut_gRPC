package exercise2;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.exercise.decomposition.DecompositionRequest;
import io.grpc.exercise.decomposition.DecompositionResponse;
import io.grpc.exercise.decomposition.DecompositionServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class DecompositionServer {
    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Hello gRPC");

        Server server = ServerBuilder.forPort(50051)
                .addService(new DecomposistionServiceImpl())
                .build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        server.awaitTermination();
    }

    private static class DecomposistionServiceImpl extends DecompositionServiceGrpc.DecompositionServiceImplBase {
        public void getDecomposition(DecompositionRequest request, StreamObserver<DecompositionResponse> responseObserver) {
            try {
                int number = request.getNumber();
                int prime = 2;
                while (number > 1) {
                    if (number % prime == 0) {
                        number /= prime;
                        DecompositionResponse response = DecompositionResponse.newBuilder().setPrime(prime).build();
                        responseObserver.onNext(response);
                        Thread.sleep(1000L);
                    } else {
                        prime++; // for "prime" numbers like 4, 6, will be skipped
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                responseObserver.onCompleted();
            }
        }
    }
}
