package com.example.demo;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.envoyproxy.envoy.config.cluster.v3.Cluster;
import io.envoyproxy.envoy.config.listener.v3.Listener;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LdsDecoder {
    public String getTypeUrl() {
        return XdsTypeUrl.LDS.getTypeUrl();
    }

    protected Map<String, Set<String>> decodeDiscoveryResponse (DiscoveryResponse response) {
        Map<String, Set<String>> map = new HashMap<>();
        List<Any> resourcesList = response.getResourcesList();
        if (getTypeUrl().equals(response.getTypeUrl())) {
            for (Any resource : resourcesList) {
                // 각 리소스를 RouteConfiguration으로 변환합니다.
                Listener listener = unpackListenerConfiguration(resource);

                // 변환에 실패한 경우 무시합니다.
                if (listener != null) {
                    // RouteConfiguration을 Map<String, Set<String>>으로 변환합니다.
//                    Map<String, Set<String>> decodedMap = decodeResourceToListener(cluster);
                    System.out.println("Listener = " + listener);
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

    private static Listener unpackListenerConfiguration(Any any) {
        try {
            return any.unpack(Listener.class);
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Error occur when decode xDS response." + e);// log
            return null;
        }
    }
}
