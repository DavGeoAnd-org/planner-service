package com.davgeoand.api;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ServiceRunner {
    @WithSpan(kind = SpanKind.INTERNAL, value = "Service Start")
    public static void main(String[] args) {
        ServiceProperties.init(
                Boolean.parseBoolean(StringUtils.defaultIfBlank(System.getenv("SET_OTLP"), "false")),
                "build.properties");
        new JavalinService().start();
    }
}