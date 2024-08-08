package com.example.demo;

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
        // String typeUrl = XdsTypeUrl.LDS.getTypeUrl(); // 변경
        String typeUrl = XdsTypeUrl.EDS.getTypeUrl();
//        String typeUrl = XdsTypeUrl.RDS.getTypeUrl();
        DiscoveryRequest request = xdsClient.buildDiscoveryRequest(typeUrl, Collections.emptySet());

        assertEquals(typeUrl, request.getTypeUrl(), "The type URL should match the input value.");
    }

    @Test
    public void testSendDiscoveryRequest() throws Exception {
//         String typeUrl = XdsTypeUrl.LDS.getTypeUrl();            // LDS
//         String typeUrl = XdsTypeUrl.CDS.getTypeUrl();              // CDS
//        String typeUrl = XdsTypeUrl.RDS.getTypeUrl();      // RDS
        String typeUrl = XdsTypeUrl.EDS.getTypeUrl();  // EDS

        // StreamObserver<DiscoveryRequest> requestStreamObserverEDS = xdsClient.sendDiscoveryRequest(typeUrl);


        // StreamObserver<DiscoveryRequest> requestStreamObserverLDS = xdsClient.sendDiscoveryRequest(XdsTypeUrl.LDS.getTypeUrl());


        // StreamObserver<DiscoveryRequest> requestStreamObserverRDS = xdsClient.sendDiscoveryRequest(XdsTypeUrl.RDS.getTypeUrl());
        // State state = new State();
        // state.setKeyName(XdsTypeUrl.CDS.getTypeUrl());

        XdsClient._State state = xdsClient.sendDiscoveryRequest(XdsTypeUrl.CDS.getTypeUrl());
         StreamObserver<DiscoveryRequest> requestStreamObserverCDS = state.streamObserverRequest;


        while(true) {
            Thread.sleep(5000);
            System.out.println("Test");
            // requestStreamObserverEDS.onNext(xdsClient.buildDiscoveryRequest(typeUrl, Collections.emptySet()));
            // requestStreamObserverLDS.onNext(xdsClient.buildDiscoveryRequest(typeUrl, Collections.emptySet()));
            // requestStreamObserverRDS.onNext(xdsClient.buildDiscoveryRequest(typeUrl, Collections.emptySet()));
             state.streamObserverRequest.onNext(state.request);
            //if (state.response != null)
            //   state.streamObserverResponse.onNext(state.response);
        }


        // 간단한 시간 대기 (서버 응답을 기다리기 위해)
        // Thread.sleep(10000); // 서버 응답을 기다리는 시간 (필요에 따라 조정)

        // 실제 요청 및 응답을 확인하기 위해서 별도의 검증 로직을 추가할 수 있습니다.
        // 예를 들어, 수신한 DiscoveryResponse를 저장하고 검증하는 로직을 추가합니다.
    }
}