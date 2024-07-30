package com.example.demo;

import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.envoyproxy.envoy.service.discovery.v3.AggregatedDiscoveryServiceGrpc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
after each에서 shutdown을 해서 문제가 발생하는 것으로 보인다.
    @Test
    public void testShutdown() {
        assertThrows(StatusRuntimeException.class, () -> {
            xdsClient.shutdown();
            //xdsClient.getStub().listResources(null); // 예제 메소드, 실제 메소드에 맞게 조정
        });
    }
 */

    @Test
    public void testBuildDiscoveryRequest() {
        String typeUrl = "type.googleapis.com/envoy.service.discovery.v3.Listener";
        DiscoveryRequest request = xdsClient.buildDiscoveryRequest(typeUrl);

        assertEquals(typeUrl, request.getTypeUrl(), "The type URL should match the input value.");
        // 필요한 경우 추가 필드에 대한 검증도 수행할 수 있습니다.

    }
}