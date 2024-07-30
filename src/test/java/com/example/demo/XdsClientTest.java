package com.example.demo;
//
//import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
//import io.grpc.ManagedChannel;
//import io.grpc.ManagedChannelBuilder;
//import io.grpc.StatusRuntimeException;
//import io.envoyproxy.envoy.service.discovery.v3.AggregatedDiscoveryServiceGrpc;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class XdsClientTest {
//    private XdsClient xdsClient;
//    private ManagedChannel channel;
//
//    @BeforeEach
//    public void setUp() {
//        // 테스트용 ManagedChannel을 생성합니다.
//        channel = ManagedChannelBuilder.forAddress("localhost", 9002) // 적절한 주소와 포트로 설정
//                .usePlaintext()
//                .build();
//        xdsClient = new XdsClient(channel);
//        System.out.println("setUp,  channel().toString() : " + channel.toString());
//    }
//
//    @AfterEach
//    public void tearDown() throws InterruptedException {
//        xdsClient.shutdown();
//    }
//
//    @Test
//    public void testGetStub() {
//        AggregatedDiscoveryServiceGrpc.AggregatedDiscoveryServiceStub stub = xdsClient.getStub();
//        assertNotNull(stub, "Stub should not be null");
//        System.out.println("TEST GET STUB,  stub.toString() : " + stub.toString());
//        System.out.println("TEST GET STUB,  stub.getChannel().toString() : " + stub.getChannel().toString());
//    }
///*
//after each에서 shutdown을 해서 문제가 발생하는 것으로 보인다.
//    @Test
//    public void testShutdown() {
//        assertThrows(StatusRuntimeException.class, () -> {
//            xdsClient.shutdown();
//            //xdsClient.getStub().listResources(null); // 예제 메소드, 실제 메소드에 맞게 조정
//        });
//    }
// */
//
//    @Test
//    public void testBuildDiscoveryRequest() {
//        String typeUrl = "type.googleapis.com/envoy.service.discovery.v3.Listener";
//        DiscoveryRequest request = xdsClient.buildDiscoveryRequest(typeUrl);
//
//        assertEquals(typeUrl, request.getTypeUrl(), "The type URL should match the input value.");
//        // 필요한 경우 추가 필드에 대한 검증도 수행할 수 있습니다.
//    }
//
//    @Test
//    public void testBuildDiscoverySend() {
//        String typeUrl = "type.googleapis.com/envoy.service.discovery.v3.Listener";
//        DiscoveryRequest request = xdsClient.buildDiscoveryRequest(typeUrl);
//
//        assertEquals(typeUrl, request.getTypeUrl(), "The type URL should match the input value.");
//        // 필요한 경우 추가 필드에 대한 검증도 수행할 수 있습니다.
//    }
//}

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.envoyproxy.envoy.service.discovery.v3.AggregatedDiscoveryServiceGrpc;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;
import io.envoyproxy.envoy.service.discovery.v3.Resource;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

//    @Test
//    public void testShutdown() {
//        assertThrows(StatusRuntimeException.class, () -> {
//            xdsClient.shutdown();
//            // 실제 메소드 호출: 이미 shutdown된 후에 호출하면 예외가 발생할 수 있음.
//        });
//    }

    @Test
    public void testBuildDiscoveryRequest() {
        // String typeUrl = "type.googleapis.com/envoy.service.discovery.v3.Listener";
        String typeUrl = "type.googleapis.com/envoy.api.v3.Cluster";
        DiscoveryRequest request = xdsClient.buildDiscoveryRequest(typeUrl, Collections.emptySet());

        assertEquals(typeUrl, request.getTypeUrl(), "The type URL should match the input value.");
    }

    @Test
    public void testSendDiscoveryRequest() throws Exception {
        // String typeUrl = "type.googleapis.com/envoy.service.discovery.v3.Listener"; // 요청할 typeUrl
        String typeUrl = "type.googleapis.com/envoy.api.v3.Cluster";
        xdsClient.sendDiscoveryRequest(typeUrl);

        // 간단한 시간 대기 (서버 응답을 기다리기 위해)
        Thread.sleep(2000); // 서버 응답을 기다리는 시간 (필요에 따라 조정)

        // 실제 요청 및 응답을 확인하기 위해서 별도의 검증 로직을 추가할 수 있습니다.
        // 예를 들어, 수신한 DiscoveryResponse를 저장하고 검증하는 로직을 추가합니다.
    }
}