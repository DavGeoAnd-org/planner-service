package com.davgeoand.api.service;

import com.davgeoand.api.ServiceMeterRegistry;
import io.micrometer.core.instrument.Meter;

import java.util.List;

public class AdminService {
    public String health() {
        return "Healthy";
    }

    public List<Meter> metrics() {
        return ServiceMeterRegistry.getMeters();
    }
}