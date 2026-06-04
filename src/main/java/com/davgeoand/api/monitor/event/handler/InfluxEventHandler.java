package com.davgeoand.api.monitor.event.handler;

import com.davgeoand.api.ServiceProperties;
import com.davgeoand.api.monitor.event.type.Event;
import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.Point;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class InfluxEventHandler implements EventHandler {
    private final InfluxDBClient influxDBClient;
    private final Map<String, String> tagsMap = new HashMap<>();

    public InfluxEventHandler() {
        log.info("Initializing influxdb service event handler");
        influxDBClient = InfluxDBClient.getInstance(ServiceProperties.getProperty("event.handler.influxdb.host"), ServiceProperties.getProperty("event.handler.influxdb.token").toCharArray(), ServiceProperties.getProperty("event.handler.influxdb.database"));
        ServiceProperties.getOtlpProperties().forEach((key, value) -> tagsMap.put(key.toString(), value.toString()));
        log.info("Finished initializing influxdb service event handler");
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        try {
            while (true) {
                Event event = eventBlockingQueue.take();
                log.debug(event.toString());
                writePoint(event.toPoint());
            }
        } catch (Exception e) {
            log.warn("Issue writing event", e);
        }
    }

    @WithSpan
    private void writePoint(Point point) {
        point.setTags(tagsMap);
        influxDBClient.writePoint(point);
    }
}
