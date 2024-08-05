package com.example.demo.xdsDecoder;

import com.example.demo.XdsTypeUrl;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.envoyproxy.envoy.config.cluster.v3.Cluster;
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
                // 각 리소스를 RouteConfiguration으로 변환합니다.
                ClusterLoadAssignment endpoint = unpackEndpointConfiguration(resource);

                // 변환에 실패한 경우 무시합니다.
                if (endpoint != null) {
                    // RouteConfiguration을 Map<String, Set<String>>으로 변환합니다.
//                    Map<String, Set<String>> decodedMap = decodeResourceToListener(cluster);
                    System.out.println("ClusterLoadAssignment = " + endpoint);
                    // 변환된 맵을 결과 맵에 합칩니다.
                    //map.putAll(decodedMap); // decodedMap의 모든 엔트리를 결과 맵에 추가합니다.
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
