package streaming_server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.exercise.serverstreaming.GreetManyTimesRequest;
import io.grpc.exercise.serverstreaming.GreetManyTimesResponse;
import io.grpc.exercise.serverstreaming.GreetServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class GreetingServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello gRPC");

        Server server = ServerBuilder.forPort(50051)
                .addService(new GreetServiceImpl())
                .build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        server.awaitTermination();
    }

    private static class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
        public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
            String firstName = request.getFirstName();
            try {
                for (int i = 0; i < 10; ++i) {
                    String result = "Hello " + firstName + ", response number: " + i;
                    GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                            .setResult(result)
                            .build();
                    responseObserver.onNext(response);
                    Thread.sleep(1000L);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                responseObserver.onCompleted();
            }
        }
    }

}
