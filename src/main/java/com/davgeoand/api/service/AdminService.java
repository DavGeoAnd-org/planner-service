package com.davgeoand.api.service;

import com.davgeoand.api.ServiceMeterRegistry;
import com.davgeoand.api.data.MainDB;
import com.davgeoand.api.model.main.ServiceInfo;
import io.micrometer.core.instrument.Meter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@NoArgsConstructor
public class AdminService {
    private final MainDB mainDB = new MainDB();

    public String health() {
        return "Healthy";
    }

    public List<Meter> metrics() {
        return ServiceMeterRegistry.getMeters();
    }

    public void addServiceInfo() {
        mainDB.addServiceInfo(new ServiceInfo());
    }
}