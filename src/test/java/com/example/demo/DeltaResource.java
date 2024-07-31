package com.example.demo;
/**
 * A interface for resources in xDS, which can be updated by ADS delta stream
 * <br/>
 * This interface is design to unify the way of fetching data in delta stream
 * in {@link org.apache.dubbo.registry.xds.util.PilotExchanger}
 */
public interface DeltaResource<T> {
    /**
     * Get resource from delta stream
     *
     * @return the newest resource from stream
     */
    T getResource();
}
