package com.davgeoand.api;

import com.davgeoand.api.exception.ServiceException;
import io.opentelemetry.common.ComponentLoader;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.instrumentation.resources.ManifestResourceProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.autoconfigure.spi.internal.DefaultConfigProperties;
import io.opentelemetry.sdk.resources.Resource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceProperties {
    @Getter
    private static final Properties properties = new Properties();
    @Getter
    private static final Properties otlpProperties = new Properties();

    static {
        log.info("Setting service properties");
        defaultProperties();
        fileProperties();
        otlpResourceProperties();
        log.debug(properties.toString());
        log.info("Finished setting service properties");
    }

    @WithSpan
    private static void otlpResourceProperties() {
        ServiceLoader<ResourceProvider> loader = ServiceLoader.load(ResourceProvider.class);
        loader.forEach(resourceProvider -> {
            log.debug(resourceProvider.getClass().toString());
            try {
                if (resourceProvider instanceof ManifestResourceProvider manifestResourceProvider) {
                    manifestResourceProvider.shouldApply(otlpDefaultConfigProperties(), Resource.empty());
                }
                resourceProvider.createResource(otlpDefaultConfigProperties()).getAttributes().forEach(((attributeKey, attributeValue) -> {
                    log.debug("attributeKey - {}, attributeValue - {}", attributeKey.getKey(), attributeValue.toString());
                    otlpProperties.put(attributeKey.getKey(), attributeValue.toString());
                }));
            } catch (Exception e) {
                log.warn("Issue with provider {}", resourceProvider.getClass());
            }
        });

        Arrays.stream(System.getenv("OTEL_RESOURCE_DISABLED_KEYS").split(",")).forEach(otlpProperties::remove);
        properties.putAll(otlpProperties);
    }

    private static ConfigProperties otlpDefaultConfigProperties() {
        return DefaultConfigProperties.create(new HashMap<>(), ComponentLoader.forClassLoader(DefaultConfigProperties.class.getClassLoader()));
    }

    @WithSpan
    private static void fileProperties() {
        try {
            for (String file : getProperty("property.files").split(",")){
                log.debug(file);
                properties.load(ServiceProperties.class.getClassLoader().getResourceAsStream(file));
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @WithSpan
    private static void defaultProperties() {
        properties.put("port", StringUtils.defaultIfBlank(System.getenv("PORT"), "10000"));
        properties.put("context.path", StringUtils.defaultIfBlank(System.getenv("CONTEXT_PATH"), "/planner"));
        properties.put("event.handler.type", StringUtils.defaultIfBlank(System.getenv("EVENT_HANDLER_TYPE"), "log"));
        properties.put("event.handler.influxdb.host", StringUtils.defaultIfBlank(System.getenv("EVENT_HANDLER_INFLUXDB_HOST"), "http://localhost:8181"));
        properties.put("event.handler.influxdb.token", StringUtils.defaultIfBlank(System.getenv("EVENT_HANDLER_INFLUXDB_TOKEN"), "apiv3_TaL3gOCmok0FWS_OeBAocsVNs9TTC9BKcdllsa4swL6bD9Sb0H1tSlOgu6Xwwk2D6HvFBHxAL9-Wdr1T5Rr7Gg"));
        properties.put("event.handler.influxdb.database", StringUtils.defaultIfBlank(System.getenv("EVENT_HANDLER_INFLUXDB_DATABASE"), "services"));
        properties.put("property.files", StringUtils.defaultIfBlank(System.getenv("PROPERTY_FILES"), "build.properties"));
        properties.put("surrealdb.connect", StringUtils.defaultIfBlank(System.getenv("SURREALDB_CONNECT"), "http://localhost:8000"));
        properties.put("surrealdb.namespace", StringUtils.defaultIfBlank(System.getenv("SURREALDB_NAMESPACE"), "planner"));
        properties.put("surrealdb.username", StringUtils.defaultIfBlank(System.getenv("SURREALDB_USERNAME"), "root"));
        properties.put("surrealdb.password", StringUtils.defaultIfBlank(System.getenv("SURREALDB_PASSWORD"), "root"));
    }

    public static String getProperty(String property) {
        log.debug("property - {}", property);
        return Optional.ofNullable(properties.getProperty(property)).orElseThrow(() -> new ServiceException.MissingPropertyException(property));
    }
}