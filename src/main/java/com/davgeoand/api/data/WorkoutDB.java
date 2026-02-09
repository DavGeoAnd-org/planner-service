package com.davgeoand.api.data;

import com.davgeoand.api.ServiceProperties;
import com.davgeoand.api.exception.JavalinServiceException;
import com.surrealdb.Surreal;
import com.surrealdb.signin.Root;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkoutDB {
    private final Surreal driver;
    private final String SURREALDB_CONNECT = ServiceProperties.getProperty("surrealdb.connect").orElseThrow(() -> new JavalinServiceException.MissingPropertyException("surrealdb.connect"));
    private final String SURREALDB_NAMESPACE = ServiceProperties.getProperty("surrealdb.namespace").orElseThrow(() -> new JavalinServiceException.MissingPropertyException("surrealdb.namespace"));
    private final String SURREALDB_USERNAME = ServiceProperties.getProperty("surrealdb.username").orElseThrow(() -> new JavalinServiceException.MissingPropertyException("surrealdb.username"));
    private final String SURREALDB_PASSWORD = ServiceProperties.getProperty("surrealdb.password").orElseThrow(() -> new JavalinServiceException.MissingPropertyException("surrealdb.password"));

    public WorkoutDB() {
        log.info("Initializing workout db");
        driver = new Surreal();
        connect();
        log.info("Initialized workout db");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    private void connect() {
        log.info("Connecting to workout db");
        driver.connect(SURREALDB_CONNECT)
                .useNs(SURREALDB_NAMESPACE)
                .useDb("Workout")
                .signin(new Root(SURREALDB_USERNAME, SURREALDB_PASSWORD));
        log.info("Connected to workout db");
    }
}
