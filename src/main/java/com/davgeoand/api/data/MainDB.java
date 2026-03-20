package com.davgeoand.api.data;

import com.davgeoand.api.ServiceProperties;
import com.davgeoand.api.exception.JavalinServiceException;
import com.davgeoand.api.model.main.ServiceInfo;
import com.surrealdb.Surreal;
import com.surrealdb.signin.RootCredential;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainDB {
    private final Surreal driver;
    private final String SURREALDB_CONNECT = ServiceProperties.getProperty("surrealdb.connect").orElseThrow(() -> new JavalinServiceException.MissingPropertyException("surrealdb.connect"));
    private final String SURREALDB_USERNAME = ServiceProperties.getProperty("surrealdb.username").orElseThrow(() -> new JavalinServiceException.MissingPropertyException("surrealdb.username"));
    private final String SURREALDB_PASSWORD = ServiceProperties.getProperty("surrealdb.password").orElseThrow(() -> new JavalinServiceException.MissingPropertyException("surrealdb.password"));

    public MainDB() {
        log.info("Initializing main db");
        driver = new Surreal();
        connect();
        log.info("Initialized main db");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    private void connect() {
        log.info("Connecting to main db");
        driver.connect(SURREALDB_CONNECT)
                .useNs("main")
                .useDb("main")
                .signin(new RootCredential(SURREALDB_USERNAME, SURREALDB_PASSWORD));
        log.info("Connected to main db");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public void addServiceInfo(ServiceInfo serviceInfo) {
        log.debug("serviceInfo - {}", serviceInfo);
        driver.create(serviceInfo.getId(), serviceInfo);
    }
}