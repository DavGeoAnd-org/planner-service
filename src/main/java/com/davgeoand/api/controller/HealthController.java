package com.davgeoand.api.controller;

import com.davgeoand.api.model.health.WeightRecord;
import com.davgeoand.api.model.response.ZonedDateTimeResponse;
import com.davgeoand.api.service.HealthService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import static io.javalin.apibuilder.ApiBuilder.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HealthController {
    public final static HealthService weightService = new HealthService();

    public static @NotNull EndpointGroup getHealthEndpoints() {
        return () -> path("weightRecords", () -> {
            get(HealthController::allWeightRecords);
            post(HealthController::addWeightRecord);
        });
    }

    private static void addWeightRecord(@NotNull Context context) {
        log.debug("request - addWeightRecord");
        WeightRecord weightRecord = context.bodyAsClass(WeightRecord.class);
        log.debug("weightRecord - {}", weightRecord);
        context.json(
                        new ZonedDateTimeResponse("Added weight", weightService.addWeightRecord(weightRecord).getTimestamp()))
                .status(HttpStatus.CREATED);
    }

    private static void allWeightRecords(@NotNull Context context) {
        log.debug("request - allWeightRecords");
        context.json(weightService.allWeightRecords())
                .status(HttpStatus.OK);
    }
}