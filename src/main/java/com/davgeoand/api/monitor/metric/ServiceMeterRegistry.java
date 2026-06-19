package com.davgeoand.api.monitor.metric;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceMeterRegistry {
    @Getter
    protected static MeterRegistry meterRegistry;

    static {
        Metrics.globalRegistry.getRegistries().stream().filter(registry -> registry.getClass().getName().contains("OpenTelemetryMeterRegistry")).findAny().ifPresentOrElse((foundRegistry) -> meterRegistry = foundRegistry, () -> meterRegistry = new SimpleMeterRegistry());
    }

    public static List<Meter> getMeters() {
        return meterRegistry.getMeters();
    }

    public static Gauge registerGaugeMeter(String name, Supplier<Number> value, String description, String unit) {
        return Gauge.builder(name, value).description(description).baseUnit(unit).register(meterRegistry);
    }

    public static Counter registerCounterMeter(String name, String description, String unit) {
        return Counter.builder(name).description(description).baseUnit(unit).register(meterRegistry);
    }
}
