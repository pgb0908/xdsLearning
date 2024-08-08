package com.example.demo;

import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;
import io.grpc.stub.StreamObserver;


public class State {
    String keyName;
    StreamObserver<DiscoveryRequest> streamObserverRequest;
    StreamObserver<DiscoveryResponse> streamObserverResponse;
    DiscoveryRequest request;
    DiscoveryResponse response;

    public State () {
    }

    public State(StreamObserver<DiscoveryRequest> streamObserverRequest,StreamObserver<DiscoveryResponse> streamObserverResponse, DiscoveryRequest request) {
        this.streamObserverRequest = streamObserverRequest;
        this.streamObserverResponse = streamObserverResponse;
        this.request = request;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setResponse(DiscoveryResponse response) {
        this.response = response;
    }

    public void setRequest(DiscoveryRequest request) {
        this.request = request;
    }

    public void setStreamObserverRequest(StreamObserver<DiscoveryRequest> streamObserverRequest) {
        this.streamObserverRequest = streamObserverRequest;
    }

    public void setStreamObserverResponse(StreamObserver<DiscoveryResponse> streamObserverResponse) {
        this.streamObserverResponse = streamObserverResponse;
    }
}
