package com.davgeoand.api;

import com.davgeoand.api.helper.ServiceProperties;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceRunner {
    @WithSpan(kind = SpanKind.INTERNAL, value = "Service Start")
    public static void main(String[] args) {
        ServiceProperties.init("build.properties");
        new JavalinService().start();
    }
}