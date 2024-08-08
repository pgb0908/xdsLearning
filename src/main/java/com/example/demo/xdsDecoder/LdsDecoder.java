package com.example.demo.xdsDecoder;

import com.example.demo.XdsTypeUrl;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.envoyproxy.envoy.config.cluster.v3.Cluster;
import io.envoyproxy.envoy.config.listener.v3.Listener;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LdsDecoder implements XdsDecoder {
    @Override
    public String getTypeUrl() {
        return XdsTypeUrl.LDS.getTypeUrl();
    }

    @Override
    public Map<String, Set<String>> decodeDiscoveryResponse (DiscoveryResponse response) {
        Map<String, Set<String>> map = new HashMap<>();
        List<Any> resourcesList = response.getResourcesList();
        if (getTypeUrl().equals(response.getTypeUrl())) {
            for (Any resource : resourcesList) {
                Listener listener = unpackListenerConfiguration(resource);
                if (listener != null) {
                    System.out.println("Listener = " + listener);
                }
            }
        }
        if (map.isEmpty())
            return new HashMap<String, Set<String>>();
        else
            return map;
    }

    private Listener unpackListenerConfiguration(Any any) {
        try {
            return any.unpack(Listener.class);
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Error occur when decode xDS response." + e);// log
            return null;
        }
    }
}
