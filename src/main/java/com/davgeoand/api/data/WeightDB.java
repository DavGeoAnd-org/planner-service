package com.davgeoand.api.data;

import com.davgeoand.api.Constants;
import com.davgeoand.api.model.weight.WeightRecord;
import com.surrealdb.Surreal;
import com.surrealdb.signin.Root;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

@Slf4j
public class WeightDB {
    private final Surreal driver;

    public WeightDB() {
        log.info("Initializing weight db");
        driver = new Surreal();
        driver.connect(Constants.SURREALDB_CONNECT)
                .useNs(Constants.SURREALDB_NAMESPACE)
                .useDb("weight")
                .signin(new Root(Constants.SURREALDB_USERNAME, Constants.SURREALDB_PASSWORD));
        log.info("Initialized weight db");
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
