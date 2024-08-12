package com.example.demo;

import io.envoyproxy.envoy.config.core.v3.Node;
import io.envoyproxy.envoy.service.discovery.v3.AggregatedDiscoveryServiceGrpc;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class NewXdsClient {

    private final ManagedChannel channel;
    private final AggregatedDiscoveryServiceGrpc.AggregatedDiscoveryServiceStub asyncStub;

    /**
     * 생성자1
     * @param host
     * @param port
     */
    public NewXdsClient(String host, int port) {
        String target = host + ":" + port;
        channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();
/*        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();*/
        this.asyncStub = AggregatedDiscoveryServiceGrpc.newStub(channel);
        this.states = new HashMap<>();
        this.requestQueue = new ArrayDeque<>();
    }



    //////////////////////////////////////////////////////////////////////////////////

    // 통신

    public void requestChat(DiscoveryRequest request) {
        System.out.println("before request data : \n" + request);

        AtomicReference<StreamObserver<DiscoveryRequest>> requestObserverRef = new AtomicReference<>();

        StreamObserver<DiscoveryResponse> responseObserver = new StreamObserver<DiscoveryResponse>() {
            @Override
            public void onNext(DiscoveryResponse response) {

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


        StreamObserver<DiscoveryRequest> requestObserver
                = asyncStub.streamAggregatedResources(new StreamObserver<DiscoveryResponse>() {
            @Override
            public void onNext(DiscoveryResponse data) {
                System.out.println("response data : \n" + data);

                DiscoveryRequest request2 = DiscoveryRequest.newBuilder().build();
                requestObserverRef.get().onNext(request2);

                responseObserver.onNext(data);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error receiving response: " + t.getMessage());
                t.printStackTrace(); // 스택 트레이스를 출력하여 상세한 오류를 확인
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream-response completed");
            }

        });

        try{
            requestObserverRef.set(requestObserver);
            requestObserver.onNext(request);
        }catch (RuntimeException e){
            requestObserver.onError(e);
            throw e;
        }

    }


    public void responseChat(DiscoveryResponse response) {


        StreamObserver<DiscoveryResponse> responseObserver
                = asyncStub.streamAggregatedResources(new StreamObserver<DiscoveryRequest>() {

            @Override
            public void onNext(DiscoveryRequest value) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        });



    }



    /////////////////////////////////////////////////////////////////


/*    public AggregatedDiscoveryServiceGrpc.AggregatedDiscoveryServiceStub getStub() {
        return stub;
    }*/

    private Queue<String> requestQueue;

    public class State {
        private DiscoveryRequest request;
        private boolean subscribed;
        private List<String> watched_resources;


        public State() {
            this.request =  DiscoveryRequest.newBuilder().build();
            this.subscribed = false;
            this.watched_resources = new ArrayList<>();
        }

        public void subscribed(){
            this.subscribed = true;
        }
    }

    private Map<String, State> states;


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }


    public void subscribeCds(){
        String type_url = "type.googleapis.com/envoy.config.cluster.v3.Cluster";

        State state = new State();
        this.states.put(type_url, state);

        requestQueue.add(type_url);
    }

    public void detectRequestQueue(){

        // request-queue 감지
        if (!requestQueue.isEmpty()) {
            while (!requestQueue.isEmpty()) {
                String url = requestQueue.poll();
                sendDiscoveryRequest(url);

            }

        } else {
            // request-queue 아무것도 없다면 receive를 하기 위한 대기
            //receiveResponse(adsTotalResource);

        }

    }

    private void sendDiscoveryRequest(String type_url){
        // state를 확인하여 구독 or ack 인지 확인
        // 그 상태에 맞는 request로 변환하여
        // 서버로 전송
        State state  = states.get(type_url);

        // 구독에 대한 리소스 초기화
        state.request = state.request.toBuilder().clearResourceNames().build();


        // 중복된 리소스를 제외하고 request의 리소스를 업데이트
        Set<String> resources = new HashSet<>();
        for (String watchedResource : state.watched_resources) {
            if (!resources.contains(watchedResource)) {
                resources.add(watchedResource);
                state.request.getResourceNamesList().add(watchedResource);
            }
        }

        if (!state.subscribed) {
            // 구독에 대한 reqeust 설정
            Node updateNode = state.request.getNode().toBuilder().setId("test-id").setCluster("test-cluster").build();
            state.request = state.request.toBuilder().setNode(updateNode).setTypeUrl(type_url).build();
            state.subscribed();

        } else {
            // ack에 대한 request 설정
            state.request = state.request.toBuilder().clearNode().build();
        }

        try {
            requestChat(state.request);
        }catch (RuntimeException e){
            e.printStackTrace();
        }

    }

}
