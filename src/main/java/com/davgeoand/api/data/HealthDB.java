package com.davgeoand.api.data;

import com.davgeoand.api.Constants;
import com.davgeoand.api.model.health.WeightRecord;
import com.surrealdb.Surreal;
import com.surrealdb.signin.Root;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

@Slf4j
public class HealthDB {
    private final Surreal driver;

    public HealthDB() {
        log.info("Initializing health db");
        driver = new Surreal();
        driver.connect(Constants.SURREALDB_CONNECT)
                .useNs(Constants.SURREALDB_NAMESPACE)
                .useDb("health")
                .signin(new Root(Constants.SURREALDB_USERNAME, Constants.SURREALDB_PASSWORD));
        log.info("Initialized health db");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<WeightRecord> allWeightRecords() {
        return driver.select(WeightRecord.class, "weightRecords");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public WeightRecord addWeightRecord(WeightRecord weightRecord) {
        log.debug("weightRecord - {}", weightRecord);
        return driver.create(WeightRecord.class, "weightRecords", weightRecord).getFirst();
    }
}
