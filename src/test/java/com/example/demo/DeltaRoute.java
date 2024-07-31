//package com.example.demo;
//
//
//import java.util.Collection;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class DeltaRoute implements DeltaResource<RouteResult> {
//    private final Map<String, Map<String, Set<String>>> data = new ConcurrentHashMap<>();
//
//    public void addResource(String resourceName, Map<String, Set<String>> route) {
//        data.put(resourceName, route);
//    }
//
//    public void removeResource(Collection<String> resourceName) {
//        if (CollectionUtils.isNotEmpty(resourceName)) {
//            resourceName.forEach(data::remove);
//        }
//    }
//
//    @Override
//    public RouteResult getResource() {
//        Map<String, Set<String>> result = new ConcurrentHashMap<>();
//        data.values().forEach(result::putAll);
//        return new RouteResult(result);
//    }
//}