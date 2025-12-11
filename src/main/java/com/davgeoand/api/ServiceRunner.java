package com.davgeoand.api;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ServiceRunner {
    public static void main(String[] args) {
        JavalinService javalinService;
        Span span = GlobalOpenTelemetry.getTracer("JavalinService").spanBuilder("ServiceRunner.main").startSpan();
        try (Scope ignored = span.makeCurrent()) {
            ServiceProperties.setExternalProperties(
                    Boolean.parseBoolean(StringUtils.defaultIfBlank(System.getenv("SET_OTLP"), "false")),
                    "build.properties");
            javalinService = new JavalinService();
        }
        span.end();
        javalinService.start();
    }
}