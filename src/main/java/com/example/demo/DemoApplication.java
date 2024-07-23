//package com.example.demo;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//
//import io.grpc.ManagedChannel;
//import io.grpc.ManagedChannelBuilder;
//import io.grpc.stub.StreamObserver;
//import envoy.service.discovery.v3.AggregatedDiscoveryServiceGrpc;
//import envoy.service.discovery.v3.DiscoveryRequest;
//import envoy.service.discovery.v3.DiscoveryResponse;
//import envoy.config.core.v3.Node;
//import com.google.protobuf.Any;
//
//@SpringBootApplication
//public class DemoApplication {
//
//    public static void main(String[] args) {
//
//        SpringApplication.run(DemoApplication.class, args);
//        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 15010)
//                .usePlaintext()
//                .build();
//
//        AggregatedDiscoveryServiceGrpc.AggregatedDiscoveryServiceStub stub = AggregatedDiscoveryServiceGrpc.newStub(channel);
//
//        DiscoveryRequest request = DiscoveryRequest.newBuilder()
//                .setVersionInfo("")
//                .setNode(Node.newBuilder().setId("test-id").build())
//                .setTypeUrl("type.googleapis.com/envoy.api.v2.Cluster")
//                .build();
//
//        StreamObserver<DiscoveryResponse> responseObserver = new StreamObserver<DiscoveryResponse>() {
//            @Override
//            public void onNext(DiscoveryResponse response) {
//                System.out.println("Received response: " + response);
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                t.printStackTrace();
//            }
//
//            @Override
//            public void onCompleted() {
//                System.out.println("Stream completed");
//            }
//        };
//
//        StreamObserver<DiscoveryRequest> requestObserver = stub.streamAggregatedResources(responseObserver);
//        requestObserver.onNext(request);
//        // 계속해서 필요한 요청을 보낼 수 있습니다.
//    }
//}