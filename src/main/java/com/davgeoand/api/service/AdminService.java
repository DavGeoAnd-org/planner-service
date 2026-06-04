package com.davgeoand.api.service;

import com.davgeoand.api.ServiceProperties;
import com.davgeoand.api.monitor.event.ServiceEventHandler;
import com.davgeoand.api.monitor.event.type.ServiceInfo;
import com.davgeoand.api.monitor.metric.ServiceMeterRegistry;
import io.micrometer.core.instrument.Meter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Properties;

@Slf4j
@NoArgsConstructor
public class AdminService {
    public String health() {
        return "Healthy";
    }

    public List<Meter> metrics() {
        return ServiceMeterRegistry.getMeters();
    }

    public Properties properties() {
        Properties properties = ServiceProperties.getProperties();
        properties.forEach((key, value) -> {
            if (key.toString().contains("password") || key.toString().contains("token")) {
                properties.remove(key);
            }
        });
        return properties;
    }

    public void addServiceInfo() {
        ServiceEventHandler.addEvent(new ServiceInfo());
    }
}
