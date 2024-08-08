package com.example.demo.xdsDecoder;

import com.example.demo.XdsTypeUrl;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.envoyproxy.envoy.config.cluster.v3.Cluster;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;

import java.util.*;

public class CdsDecoder implements XdsDecoder {
    @Override
    public String getTypeUrl() {
        return XdsTypeUrl.CDS.getTypeUrl();
    }

    @Override
    public Map<String, Set<String>> decodeDiscoveryResponse (DiscoveryResponse response) {
        Map<String, Set<String>> map = new HashMap<>();
        List<Any> resourcesList = response.getResourcesList();
        if (getTypeUrl().equals(response.getTypeUrl())) {
            for (Any resource : resourcesList) {
                Cluster cluster = unpackClusterConfiguration(resource);

                // 변환에 실패한 경우 무시합니다.
                if (cluster != null) {
                    System.out.println("cluster = " + cluster);
                }
            }
        }
        if (map.isEmpty())
            return new HashMap<String, Set<String>>();
        else
            return map;
    }

    private Cluster unpackClusterConfiguration(Any any) {
        try {
            return any.unpack(Cluster.class);
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Error occur when decode xDS response." + e);// log
            return null;
        }
    }
}
