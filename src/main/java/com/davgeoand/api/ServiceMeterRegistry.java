package com.davgeoand.api;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceMeterRegistry {
    protected static MeterRegistry meterRegistry;

    static {
        Metrics.globalRegistry.getRegistries().stream()
                .filter(registry -> registry.getClass().getName().contains("OpenTelemetryMeterRegistry"))
                .findAny()
                .ifPresentOrElse((foundRegistry) -> {
                    log.debug("Found OpenTelemetryMeterRegistry");
                    meterRegistry = foundRegistry;
                }, () -> {
                    log.debug("Using SimpleMeterRegistry");
                    meterRegistry = new SimpleMeterRegistry();
                });
    }

    public static List<Meter> getMeters() {
        return meterRegistry.getMeters();
    }
}