package com.davgeoand.api.monitor.event;

import com.davgeoand.api.ServiceProperties;
import com.davgeoand.api.monitor.event.handler.EventHandler;
import com.davgeoand.api.monitor.event.handler.InfluxEventHandler;
import com.davgeoand.api.monitor.event.handler.LogEventHandler;
import com.davgeoand.api.monitor.event.type.Event;
import com.davgeoand.api.monitor.metric.ServiceMeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.BaseUnits;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceEventHandler {
    static EventHandler eventHandler;
    static Gauge queueSizeGauge;
    static Counter eventAddedCounter;

    public static void init() {
        log.info("Initializing service event handler");
        try {
            eventHandler = (ServiceProperties.getProperty("event.handler.type").equals("influxdb") ? new InfluxEventHandler() : new LogEventHandler());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            eventHandler = new LogEventHandler();
        }
        addMetersToMeterRegistry();
        Thread eventHandlerThread = new Thread(eventHandler);
        eventHandlerThread.setName("ServiceEventHandler");
        eventHandlerThread.start();
        log.info("Finished initializing service event handler");
    }

    private static void addMetersToMeterRegistry() {
        queueSizeGauge = ServiceMeterRegistry.registerGaugeMeter("event.queue.size", () -> eventHandler.queueSize(), "Size of the queue for ApiEventHandler", BaseUnits.EVENTS);
        eventAddedCounter = ServiceMeterRegistry.registerCounterMeter("event.added", "Amount of events added to ApiEventHandler", BaseUnits.EVENTS);
    }

    public static void addEvent(Event event) {
        eventHandler.addEventToQueue(event);
        eventAddedCounter.increment();
    }
}
