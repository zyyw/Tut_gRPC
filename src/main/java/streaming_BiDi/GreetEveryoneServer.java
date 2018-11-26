package streaming_BiDi;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.exercise.BiDiStreaming.GreetEveryoneRequest;
import io.grpc.exercise.BiDiStreaming.GreetEveryoneResponse;
import io.grpc.exercise.BiDiStreaming.GreetEveryoneServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class GreetEveryoneServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello gRPC");

        Server server = ServerBuilder.forPort(50051)
                .addService(new GreetEveryoneServiceImpl())
                .build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        server.awaitTermination();
    }

    private static class GreetEveryoneServiceImpl extends GreetEveryoneServiceGrpc.GreetEveryoneServiceImplBase {
        public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
            StreamObserver<GreetEveryoneRequest> requestObserver = new StreamObserver<GreetEveryoneRequest>() {
                @Override
                public void onNext(GreetEveryoneRequest greetEveryoneRequest) {
                    String result = "Hello " + greetEveryoneRequest.getFirstName();
                    GreetEveryoneResponse greetEveryoneResponse = GreetEveryoneResponse.newBuilder().setResult(result).build();
                    responseObserver.onNext(greetEveryoneResponse);
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };
            return requestObserver;
        }
    }
}
