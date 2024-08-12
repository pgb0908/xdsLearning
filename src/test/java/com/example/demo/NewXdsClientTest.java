package com.example.demo;

import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NewXdsClientTest {

    private NewXdsClient xdsClient;
    @BeforeEach
    public void setUp() {
        // 실제 xDS 서버에 연결할 ManagedChannel을 생성합니다.

        xdsClient = new NewXdsClient("localhost", 9002);
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        xdsClient.shutdown();
    }

    @Test
    public void testSendDiscoveryRequest() throws Exception {
        xdsClient.subscribeCds();
        xdsClient.detectRequestQueue();

    }
}
