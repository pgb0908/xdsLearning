package com.example.demo;

import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class XdsReader {
    // type URL
    // -> while, loop out : connection fail
    // onNext?
    // can i know there is any sign when it is changed?
    //

    public static void runXds (XdsClient xdsClient, XdsTypeUrl xdsTypeUrl) {
        Callable<Integer> task = () -> {

            XdsReader.listenXds(xdsClient,xdsTypeUrl);
            return 0;
        };
        FutureTask<Integer> future = new FutureTask<>(task);
        new Thread(future).start();
    }
    public static void listenXds(XdsClient xdsClient, XdsTypeUrl xdsTypeUrl) throws InterruptedException {
        // start


        String typeUrl = xdsTypeUrl.getTypeUrl();  // EDS

        XdsClient._State state = xdsClient.sendDiscoveryRequest(typeUrl);
        StreamObserver<DiscoveryRequest> requestStreamObserverXds = state.streamObserverRequest;


        while(true) {
            Thread.sleep(5000);
            state.streamObserverRequest.onNext(state.request);
        }
        // connection이 일부가 깨지거나
        // server에 connection이 모두 깨질 때
        // 그에 대한 처리 방안이 필요하다.

        // end
    }
}
