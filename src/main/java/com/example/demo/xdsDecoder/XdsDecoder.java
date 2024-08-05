package com.example.demo.xdsDecoder;

import com.example.demo.XdsTypeUrl;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;

import java.util.Map;
import java.util.Set;

public interface XdsDecoder {
    public String getTypeUrl();
    public Map<String, Set<String>> decodeDiscoveryResponse (DiscoveryResponse response);
}
