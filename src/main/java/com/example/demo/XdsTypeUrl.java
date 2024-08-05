package com.example.demo;

public enum XdsTypeUrl {
    CDS("type.googleapis.com/envoy.config.cluster.v3.Cluster")
    ,EDS("type.googleapis.com/envoy.config.endpoint.v3.ClusterLoadAssignment")
    ,RDS("type.googleapis.com/envoy.config.route.v3.RouteConfiguration")
    ,LDS("type.googleapis.com/envoy.config.listener.v3.Listener");


    XdsTypeUrl(String typeUrl) {
        this.typeUrl = typeUrl;
    }

    private String typeUrl;

    public String getTypeUrl() {
        return this.typeUrl;
    }
}