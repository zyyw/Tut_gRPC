package exercise3;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.exercise.average.AverageRequest;
import io.grpc.exercise.average.AverageResponse;
import io.grpc.exercise.average.AverageServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class AverageServer {
    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("This is a gRPC server");

        Server server = ServerBuilder.forPort(50051)
                .addService(new AverageServiceImpl())
                .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        server.awaitTermination();
    }

    private static class AverageServiceImpl extends AverageServiceGrpc.AverageServiceImplBase {
        public StreamObserver<AverageRequest> getAverage(StreamObserver<AverageResponse> responseObserver) {
            StreamObserver<AverageRequest> requestObserver = new StreamObserver<AverageRequest>() {
                int sum = 0;
                int count = 0;
                @Override
                public void onNext(AverageRequest averageRequest) {
                    // client sends a message
                    sum += averageRequest.getNumber();
                    count++;
                }

                @Override
                public void onError(Throwable throwable) {
                    // client sends an error
                }

                @Override
                public void onCompleted() {
                    // client is done
                    double result = 0;
                    if (count != 0) {
                        result = (double) sum / (double) count;
                    }
                    responseObserver.onNext(AverageResponse.newBuilder().setResult(result).build());
                    responseObserver.onCompleted();
                }
            };
            return requestObserver;
        }
    }
}
