package streaming_client;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.exercise.clientstreaming.LongGreetRequest;
import io.grpc.exercise.clientstreaming.LongGreetResponse;
import io.grpc.exercise.clientstreaming.LongGreetServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class LongGreetServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("This is a gRPC server");

        Server server = ServerBuilder.forPort(50051)
                .addService(new LongGreetServiceImpl())
                .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        server.awaitTermination();
    }

    private static class LongGreetServiceImpl extends LongGreetServiceGrpc.LongGreetServiceImplBase {
        public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
            StreamObserver<LongGreetRequest> requestObserver = new StreamObserver<LongGreetRequest>() {
                String result = "";

                @Override
                public void onNext(LongGreetRequest longGreetRequest) {
                    // client sends a message
                    result += "Hello " + longGreetRequest.getFirstName() + "! ";
                }

                @Override
                public void onError(Throwable throwable) {
                    // client sends an error
                }

                @Override
                public void onCompleted() {
                    // client is done
                    responseObserver.onNext(
                            LongGreetResponse.newBuilder()
                            .setResult(result)
                            .build()
                    );
                    responseObserver.onCompleted();
                }
            };
            return requestObserver;
        }

    }
}
