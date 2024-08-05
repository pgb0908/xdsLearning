package com.example.demo.xdsDecoder;

import com.example.demo.XdsTypeUrl;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.envoyproxy.envoy.config.route.v3.Route;
import io.envoyproxy.envoy.config.route.v3.RouteConfiguration;
import io.envoyproxy.envoy.config.route.v3.VirtualHost;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;
import io.envoyproxy.envoy.service.discovery.v3.Resource;

import java.util.*;

public class RdsDecoder implements XdsDecoder {
    @Override
    public String getTypeUrl() {
        return XdsTypeUrl.RDS.getTypeUrl();
    }
    @Override
    public Map<String, Set<String>> decodeDiscoveryResponse (DiscoveryResponse response) {
        Map<String, Set<String>> map = new HashMap<>();
        List<Any> resourcesList = response.getResourcesList();

        if (getTypeUrl().equals(response.getTypeUrl())) {
            for (Any resource : resourcesList) {
                // 각 리소스를 RouteConfiguration으로 변환합니다.
                RouteConfiguration routeConfiguration = unpackRouteConfiguration(resource);

                // 변환에 실패한 경우 무시합니다.
                if (routeConfiguration != null) {
                    // RouteConfiguration을 Map<String, Set<String>>으로 변환합니다.
                    Map<String, Set<String>> decodedMap = decodeResourceToListener(routeConfiguration);

                    // 변환된 맵을 결과 맵에 합칩니다.
                    map.putAll(decodedMap); // decodedMap의 모든 엔트리를 결과 맵에 추가합니다.
                }
            }
        }
        if (map.isEmpty())
            return new HashMap<String, Set<String>>();
        else
            return map;
    }

    public Map<String, Set<String>> decodeResourceToListener(RouteConfiguration resource) {
        Map<String, Set<String>> map = new HashMap<>();
// 리소스에서 가상 호스트 목록을 가져옵니다.
        System.out.println("RouteConfiguration :  " + resource);
        List<VirtualHost> virtualHosts = resource.getVirtualHostsList();

// 각 가상 호스트를 처리합니다.
        for (VirtualHost virtualHost : virtualHosts) {
            // 현재 가상 호스트의 경로 목록에서 클러스터 집합을 생성합니다.
            Set<String> clusterSet = new HashSet<>(); // 클러스터를 저장할 집합을 초기화합니다.

            // 가상 호스트의 각 경로를 반복합니다.
            for (Route route : virtualHost.getRoutesList()) {
                // 경로에서 RouteAction을 가져오고, 클러스터 이름을 추출하여 집합에 추가합니다.
                String cluster = route.getRoute().getCluster(); // 클러스터 이름을 가져옵니다.
                clusterSet.add(cluster); // 클러스터 집합에 추가합니다.
            }

            // 현재 가상 호스트의 도메인 목록을 가져옵니다.
            List<String> domains = virtualHost.getDomainsList();

            // 각 도메인에 대해 클러스터 집합을 맵에 추가합니다.
            for (String domain : domains) {
                map.put(domain, clusterSet); // 도메인을 키로, 클러스터 집합을 값으로 추가합니다.
            }
        }
        return map;
    }


    private RouteConfiguration unpackRouteConfiguration(Any any) {
        try {
            return any.unpack(RouteConfiguration.class);
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Error occur when decode xDS response." + e);// log
            return null;
        }
    }
}
