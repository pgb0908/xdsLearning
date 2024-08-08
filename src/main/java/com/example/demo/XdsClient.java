package com.example.demo;
import com.example.demo.xdsDecoder.*;
import io.envoyproxy.envoy.config.core.v3.Node;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.envoyproxy.envoy.service.discovery.v3.AggregatedDiscoveryServiceGrpc;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;
import io.grpc.stub.StreamObserver;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class XdsClient {
    private final ManagedChannel channel;
    private final AggregatedDiscoveryServiceGrpc.AggregatedDiscoveryServiceStub stub;



    protected final static AtomicLong requestId = new AtomicLong(0);
    /**
     * Store Request Parameter ( resourceNames )
     * K - requestId, V - resourceNames
     */
    protected final Map<Long, Set<String>> requestParam = new ConcurrentHashMap<>();

    /**
     * Store ADS Request Observer ( StreamObserver in Streaming Request )
     * K - requestId, V - StreamObserver
     */
    private final Map<Long, StreamObserver<DiscoveryRequest>> requestObserverMap = new ConcurrentHashMap<>();
    private final Map<Long, StreamObserver<DiscoveryResponse>> responseObserverMap = new ConcurrentHashMap<>();
    private DiscoveryResponse responsed;


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


    public DiscoveryRequest buildDiscoveryRequest(String typeUrl, Set<String> resourceNames) {
        Node node = Node.newBuilder().setId("test-id").setCluster("test-cluster").build();
        return DiscoveryRequest.newBuilder()
                .setTypeUrl(typeUrl)
                .addAllResourceNames(resourceNames)
                .setNode(node)
                .build();
    }
    public class _State {
        StreamObserver<DiscoveryRequest> streamObserverRequest;
        StreamObserver<DiscoveryResponse> streamObserverResponse;
        DiscoveryRequest request;
        DiscoveryResponse response;

        public _State () {

        }

        public _State(StreamObserver<DiscoveryRequest> streamObserverRequest,StreamObserver<DiscoveryResponse> streamObserverResponse, DiscoveryRequest request) {
            this.streamObserverRequest = streamObserverRequest;
            this.streamObserverResponse = streamObserverResponse;
            this.request = request;
        }

        public void setResponse(DiscoveryResponse response) {
            this.response = response;
        }
    }

    public _State sendDiscoveryRequest(String typeUrl) {
        DiscoveryRequest request = buildDiscoveryRequest(typeUrl, Collections.emptySet());

        StreamObserver<DiscoveryResponse> responseObserver = new StreamObserver<DiscoveryResponse>() {
            @Override
            public void onNext(DiscoveryResponse response) {
                if (responsed == null || ! (responsed.getVersionInfo().equals(response.getVersionInfo()))) {
                    handleDiscoveryResponse(response); // 응답 처리
                    responsed = response;
                }
                responsed = response;
                sendAck(response);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error receiving response: " + t.getMessage());
                t.printStackTrace(); // 스택 트레이스를 출력하여 상세한 오류를 확인
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed");
            }
        };

        StreamObserver<DiscoveryRequest> requestStreamObserver = stub.streamAggregatedResources(responseObserver);
        requestStreamObserver.onNext(request);
        requestObserverMap.put(1L,requestStreamObserver);
        responseObserverMap.put(1L,responseObserver);

        return new _State(requestStreamObserver, responseObserver,request);
    }



    // 응답
    private void handleDiscoveryResponse(DiscoveryResponse response) {
        processResponseData(response);
    }
    private void processResponseData(DiscoveryResponse response) {

        XdsDecoder xdsDecoder = getXdsDecoder(response);

        if (xdsDecoder == null) {
            System.out.println("Undefined Type Url.");
            return;
        }

        Map<String, Set<String>> stringSetMap = xdsDecoder.decodeDiscoveryResponse(response);
    }

    private XdsDecoder getXdsDecoder(DiscoveryResponse response) {
        XdsDecoder xdsDecoder = null;

        if (response.getTypeUrl().equals(XdsTypeUrl.RDS.getTypeUrl())) {
            xdsDecoder = new RdsDecoder();
        } else if (response.getTypeUrl().equals(XdsTypeUrl.CDS.getTypeUrl())) {
            xdsDecoder = new CdsDecoder();
        } else if (response.getTypeUrl().equals(XdsTypeUrl.LDS.getTypeUrl())) {
            xdsDecoder = new LdsDecoder();
        } else if (response.getTypeUrl().equals(XdsTypeUrl.EDS.getTypeUrl())) {
            xdsDecoder = new EdsDecoder();
        }

        return xdsDecoder;
    }

    public void sendAck(DiscoveryResponse response) {
        DiscoveryRequest ack = DiscoveryRequest.newBuilder()
                .setVersionInfo(response.getVersionInfo())
                .setResponseNonce(response.getNonce())
                .setTypeUrl(response.getTypeUrl())
                .build();
        System.out.println("SEND ACK, ack : " + ack);
        requestObserverMap.get(1L).onNext(ack);


    }
}

// https://tmaxsoft.atlassian.net/wiki/spaces/APIM/pages/641728973/~