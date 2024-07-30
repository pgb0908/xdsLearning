package com.example.demo;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.envoyproxy.envoy.service.discovery.v3.AggregatedDiscoveryServiceGrpc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class XdsClientTest {
    private XdsClient xdsClient;
    private ManagedChannel channel;

    @BeforeEach
    public void setUp() {
        // 테스트용 ManagedChannel을 생성합니다.
        channel = ManagedChannelBuilder.forAddress("localhost", 9002) // 적절한 주소와 포트로 설정
                .usePlaintext()
                .build();
        xdsClient = new XdsClient(channel);
        System.out.println("setUp,  channel().toString() : " + channel.toString());
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        xdsClient.shutdown();
    }

    @Test
    public void testGetStub() {
        AggregatedDiscoveryServiceGrpc.AggregatedDiscoveryServiceStub stub = xdsClient.getStub();
        assertNotNull(stub, "Stub should not be null");
        System.out.println("TEST GET STUB,  stub.toString() : " + stub.toString());
        System.out.println("TEST GET STUB,  stub.getChannel().toString() : " + stub.getChannel().toString());
    }
/*
    @Test
    public void testShutdown() {
        assertThrows(StatusRuntimeException.class, () -> {
            xdsClient.shutdown();
            //xdsClient.getStub().listResources(null); // 예제 메소드, 실제 메소드에 맞게 조정
        });
    }
 */
}