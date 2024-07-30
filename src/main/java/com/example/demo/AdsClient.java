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
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9002)
                .usePlaintext()
                .build();

        AggregatedDiscoveryServiceGrpc.AggregatedDiscoveryServiceStub stub = AggregatedDiscoveryServiceGrpc.newStub(channel);

        StreamObserver<DiscoveryRequest> requestObserver = stub.streamAggregatedResources(new StreamObserver<DiscoveryResponse>() {
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
        });

        DiscoveryRequest request = DiscoveryRequest.newBuilder()
                .setVersionInfo("")
                .setNode(Node.newBuilder().setId("test-id").build())
                .setTypeUrl("type.googleapis.com/envoy.api.v2.Cluster")
                .build();

        requestObserver.onNext(request);
        // Keep the stream open to receive more responses
        DiscoveryResponse response = DiscoveryResponse.newBuilder().build();
        try {
            Thread.sleep(60000); // Keep the application running for 1 minute
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Close the channel
        channel.shutdown();
    }
}