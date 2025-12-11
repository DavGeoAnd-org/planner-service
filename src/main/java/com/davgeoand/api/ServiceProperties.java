package com.davgeoand.api;

import io.opentelemetry.common.ComponentLoader;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.instrumentation.resources.ManifestResourceProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.autoconfigure.spi.internal.DefaultConfigProperties;
import io.opentelemetry.sdk.resources.Resource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceProperties {
    private static final Properties properties = new Properties();

    static {
        //Service
        properties.put("service.name", "planner-service");
        properties.put("service.port", StringUtils.defaultIfBlank(System.getenv("SERVICE_PORT"), "8080"));
        properties.put("service.context.path", StringUtils.defaultIfBlank(System.getenv("SERVICE_CONTEXT_PATH"), "/planner"));

        // SurrealDB
        properties.put("surrealdb.connect", StringUtils.defaultIfBlank(System.getenv("SURREALDB_CONNECT"), "http://localhost:8000"));
        properties.put("surrealdb.namespace", StringUtils.defaultIfBlank(System.getenv("SURREALDB_NAMESPACE"), "planner"));
        properties.put("surrealdb.username", StringUtils.defaultIfBlank(System.getenv("SURREALDB_USERNAME"), "root"));
        properties.put("surrealdb.password", StringUtils.defaultIfBlank(System.getenv("SURREALDB_PASSWORD"), "root"));
    }

    @WithSpan
    public static void setExternalProperties(boolean setOtlp, String... files) {
        log.info("Initializing service properties");
        log.debug("files - {}", Arrays.toString(files));

        if (setOtlp) {
            setOtlpProperties();
        }
        for (String file : files) {
            try {
                properties.load(ServiceProperties.class.getClassLoader().getResourceAsStream(file));
            } catch (IOException e) {
                log.error(e.getMessage());
                System.exit(1);
            }
        }

        log.debug("properties - {}", properties);
        log.info("Initialized service properties");
    }

    @WithSpan
    private static void setOtlpProperties() {
        log.info("Setting opentelemetry properties");

        ServiceLoader<ResourceProvider> loader = ServiceLoader.load(ResourceProvider.class);
        loader.forEach(resourceProvider -> {
            log.debug(resourceProvider.getClass().toString());
            try {
                if (resourceProvider instanceof ManifestResourceProvider manifestResourceProvider) {
                    manifestResourceProvider.shouldApply(otlpDefaultConfigProperties(), Resource.empty());
                }
                resourceProvider.createResource(otlpDefaultConfigProperties()).getAttributes().forEach(((attributeKey, attributeValue) -> {
                    log.debug("attributeKey - {}, attributeValue - {}", attributeKey.getKey(), attributeValue.toString());
                    properties.put(attributeKey.getKey(), attributeValue.toString());
                }));
            } catch (Exception e) {
                log.warn("Issue with provider {}", resourceProvider.getClass());
            }
        });

        log.info("Set opentelemetry properties");
    }

    private static ConfigProperties otlpDefaultConfigProperties() {
        return DefaultConfigProperties.create(new HashMap<>(), ComponentLoader.forClassLoader(DefaultConfigProperties.class.getClassLoader()));
    }

    public static Optional<String> getProperty(String property) {
        return Optional.ofNullable(properties.getProperty(property));
    }
}