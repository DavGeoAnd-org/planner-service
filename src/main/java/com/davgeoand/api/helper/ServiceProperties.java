package com.davgeoand.api.helper;

import io.opentelemetry.common.ComponentLoader;
import io.opentelemetry.instrumentation.resources.ManifestResourceProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.autoconfigure.spi.internal.DefaultConfigProperties;
import io.opentelemetry.sdk.resources.Resource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceProperties {
    private static Properties properties;

    public static void init(String... files) {
        log.info("Initializing service properties");
        log.debug("files - {}", Arrays.toString(files));

        properties = new Properties();
        for (String file : files) {
            try {
                properties.load(ServiceProperties.class.getClassLoader().getResourceAsStream(file));
            } catch (IOException e) {
                log.error(e.getMessage());
                System.exit(1);
            }
        }
        setOtlpProperties();

        log.debug("properties - {}", properties);
        log.info("Initialized service properties");
    }

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