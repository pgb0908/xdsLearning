package com.example.demo;

import io.envoyproxy.envoy.config.core.v3.Node;
import io.envoyproxy.envoy.service.discovery.v3.AggregatedDiscoveryServiceGrpc;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;


public class AdsClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 15010)
                .usePlaintext()
                .build();

        AggregatedDiscoveryServiceGrpc.AggregatedDiscoveryServiceStub stub = AggregatedDiscoveryServiceGrpc.newStub(channel);

        DiscoveryRequest request = DiscoveryRequest.newBuilder()
                .setVersionInfo("")
                .setNode(Node.newBuilder().setId("test-id").build())
                .setTypeUrl("type.googleapis.com/envoy.api.v2.Cluster")
                .build();

        StreamObserver<DiscoveryResponse> responseObserver = new StreamObserver<DiscoveryResponse>() {
            @Override
            public void onNext(DiscoveryResponse response) {
                System.out.println("Received response: " + response);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed");
            }
        };

        StreamObserver<DiscoveryRequest> requestObserver = stub.streamAggregatedResources(responseObserver);
        requestObserver.onNext(request);
    }
}
