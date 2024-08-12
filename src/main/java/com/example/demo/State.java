package com.example.demo;

import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;

import java.util.ArrayList;
import java.util.List;

public class State {
    public DiscoveryRequest request;
    public boolean subscribed;
    public List<String> watched_resources;


    public State() {
        this.request =  DiscoveryRequest.newBuilder().build();
        this.subscribed = false;
        this.watched_resources = new ArrayList<>();
    }

    public void subscribed(){
        this.subscribed = true;
    }
}
