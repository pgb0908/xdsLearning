package com.example.demo;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class XdsClientTest {
    private XdsClient xdsClient;
    private ManagedChannel channel;

    @BeforeEach
    public void setUp() {
        // 실제 xDS 서버에 연결할 ManagedChannel을 생성합니다.
        channel = ManagedChannelBuilder.forAddress("localhost", 9002) // 실제 서버 주소와 포트
                .usePlaintext()
                .build();
        xdsClient = new XdsClient(channel);
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        xdsClient.shutdown();
    }

    @Test
    public void testGetStub() {
        assertNotNull(xdsClient.getStub(), "Stub should not be null");
    }

    @Test
    public void testBuildDiscoveryRequest() {
        String typeUrl = XdsTypeUrl.EDS.getTypeUrl();
        DiscoveryRequest request = xdsClient.buildDiscoveryRequest(typeUrl, Collections.emptySet());

        assertEquals(typeUrl, request.getTypeUrl(), "The type URL should match the input value.");
    }

    @Test
    public void testSendDiscoveryRequest() throws Exception {
//         String typeUrl = XdsTypeUrl.LDS.getTypeUrl();            // LDS
//         String typeUrl = XdsTypeUrl.CDS.getTypeUrl();              // CDS
//        String typeUrl = XdsTypeUrl.RDS.getTypeUrl();      // RDS
        String typeUrl = XdsTypeUrl.EDS.getTypeUrl();  // EDS

        XdsClient._State state = xdsClient.sendDiscoveryRequest(XdsTypeUrl.CDS.getTypeUrl());
         StreamObserver<DiscoveryRequest> requestStreamObserverCDS = state.streamObserverRequest;


        while(true) {
            Thread.sleep(5000);
            System.out.println("Test");
             state.streamObserverRequest.onNext(state.request);
        }
    }
}