package com.example.demo;


import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RouteResult {
    private final Map<String, Set<String>> domainMap;

    public RouteResult() {
        this.domainMap = new ConcurrentHashMap<>();
    }

    public RouteResult(Map<String, Set<String>> domainMap) {
        this.domainMap = domainMap;
    }

    public boolean isNotEmpty() {
        return !domainMap.isEmpty();
    }

    public Set<String> searchDomain(String domain) {
        return domainMap.getOrDefault(domain, new ConcurrentHashSet<>());
    }

    public Set<String> getDomains() {
        return Collections.unmodifiableSet(domainMap.keySet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RouteResult that = (RouteResult) o;
        return Objects.equals(domainMap, that.domainMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainMap);
    }

    @Override
    public String toString() {
        return "RouteResult{" +
                "domainMap=" + domainMap +
                '}';
    }
}