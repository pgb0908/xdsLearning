package com.example.demo;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.envoyproxy.envoy.service.discovery.v3.AggregatedDiscoveryServiceGrpc;

import java.util.concurrent.TimeUnit;

public class XdsClient {
    private final ManagedChannel channel;
    private final AggregatedDiscoveryServiceGrpc.AggregatedDiscoveryServiceStub stub;

    public XdsClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.stub = AggregatedDiscoveryServiceGrpc.newStub(channel);
    }

    // 추가된 생성자: ManagedChannel 주입
    public XdsClient(ManagedChannel channel) {
        this.channel = channel;
        this.stub = AggregatedDiscoveryServiceGrpc.newStub(channel);
    }

    public AggregatedDiscoveryServiceGrpc.AggregatedDiscoveryServiceStub getStub() {
        return stub;
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}