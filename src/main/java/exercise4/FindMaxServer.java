package exercise4;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.exercise.findmax.FindMaxRequest;
import io.grpc.exercise.findmax.FindMaxResponse;
import io.grpc.exercise.findmax.FindMaxServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class FindMaxServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello gRpc");

        Server server = ServerBuilder.forPort(50051)
                .addService(new FindMaxServiceImpl())
                .build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        server.awaitTermination();
    }

    private static class FindMaxServiceImpl extends FindMaxServiceGrpc.FindMaxServiceImplBase {
        public StreamObserver<FindMaxRequest> findMax(StreamObserver<FindMaxResponse> responseObserver) {
            StreamObserver<FindMaxRequest> requestObserver = new StreamObserver<FindMaxRequest>() {
                int curMax = Integer.MIN_VALUE;

                @Override
                public void onNext(FindMaxRequest findMaxRequest) {
                    int curNumber = findMaxRequest.getCurrentNumber();
                    curMax = Math.max(curMax, curNumber);
                    FindMaxResponse findMaxResponse = FindMaxResponse.newBuilder().setCurrentMax(curMax).build();
                    responseObserver.onNext(findMaxResponse);
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
