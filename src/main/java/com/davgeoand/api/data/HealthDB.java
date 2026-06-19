package com.davgeoand.api.data;

import com.davgeoand.api.ServiceProperties;
import com.davgeoand.api.model.health.WeightRecord;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.signin.RootCredential;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class HealthDB {
    private final Surreal driver;
    private final String SURREALDB_CONNECT = ServiceProperties.getProperty("surrealdb.connect");
    private final String SURREALDB_NAMESPACE = ServiceProperties.getProperty("surrealdb.namespace");
    private final String SURREALDB_USERNAME = ServiceProperties.getProperty("surrealdb.username");
    private final String SURREALDB_PASSWORD = ServiceProperties.getProperty("surrealdb.password");

    public HealthDB() {
        try {
            log.info("Initializing health db");
            driver = new Surreal();
            connect();
            log.debug("SurrealDB Server Version: {}", driver.version());
            log.info("Initialized health db");
        } catch (Exception exception) {
            log.error("Issue initializing health db", exception);
            throw new RuntimeException();
        }
    }

    @WithSpan(kind = SpanKind.CLIENT)
    private void connect() {
        log.info("Connecting to health db");
        driver.connect(SURREALDB_CONNECT)
                .useNs(SURREALDB_NAMESPACE)
                .useDb("health")
                .signin(new RootCredential(SURREALDB_USERNAME, SURREALDB_PASSWORD));
        log.info("Connected to health db");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<WeightRecord> allWeightRecords() {
        return driver.select(WeightRecord.class, "weightRecords");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public WeightRecord addWeightRecord(WeightRecord weightRecord) {
        log.debug("weightRecord - {}", weightRecord);
        return driver.create(weightRecord.getId(), weightRecord).get(WeightRecord.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<WeightRecord> weightRecordsByDayRange(int days) {
        log.debug("days - {}", days);
        Response response = driver.query("""
                        SELECT *
                        FROM weightRecords
                        WHERE record::id(id) > $days;""",
                Map.of("days", Instant.now().minus(days, ChronoUnit.DAYS).toEpochMilli()));
        return response.take(0).getArray().iterator(WeightRecord.class);
    }
}
