package com.example.demo.xdsDecoder;

import com.example.demo.XdsTypeUrl;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.envoyproxy.envoy.config.endpoint.v3.ClusterLoadAssignment;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EdsDecoder implements XdsDecoder {
    @Override
    public String getTypeUrl() {
        return XdsTypeUrl.EDS.getTypeUrl();
    }
    @Override
    public Map<String, Set<String>> decodeDiscoveryResponse (DiscoveryResponse response) {
        Map<String, Set<String>> map = new HashMap<>();
        List<Any> resourcesList = response.getResourcesList();
        System.out.println("ClusterLoadAssignment");
        if (getTypeUrl().equals(response.getTypeUrl())) {
            for (Any resource : resourcesList) {
                ClusterLoadAssignment endpoint = unpackEndpointConfiguration(resource);

                if (endpoint != null) {
                    System.out.println("ClusterLoadAssignment = " + endpoint);
                }
            }
        }
        if (map.isEmpty())
            return new HashMap<String, Set<String>>();
        else
            return map;
    }

    private ClusterLoadAssignment unpackEndpointConfiguration(Any any) {
        try {
            return any.unpack(ClusterLoadAssignment.class);
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Error occur when decode xDS response." + e);// log
            return null;
        }
    }

}
