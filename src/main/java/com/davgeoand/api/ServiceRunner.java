package com.davgeoand.api;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceRunner {
    public static void main(String[] args) {
        JavalinService javalinService;
        Span span = GlobalOpenTelemetry.getTracer("JavalinService").spanBuilder("ServiceRunner").startSpan();
        try (Scope ignored = span.makeCurrent()) {
            javalinService = new JavalinService();
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            span.end();
        }
        javalinService.start();
    }
}