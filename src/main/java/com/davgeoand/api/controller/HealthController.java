package com.davgeoand.api.controller;

import com.davgeoand.api.model.health.WeightRecord;
import com.davgeoand.api.model.response.MessageResponse;
import com.davgeoand.api.service.HealthService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import static io.javalin.apibuilder.ApiBuilder.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HealthController {
    public final static HealthService healthService = new HealthService();

    public static @NotNull EndpointGroup getHealthEndpoints() {
        return () -> path("weightRecords", () -> {
            get(HealthController::allWeightRecords);
            post(HealthController::addWeightRecord);
            path("range", () -> {
                get("days", HealthController::weightRecordsByDayRange);
            });
        });
    }

    private static void weightRecordsByDayRange(@NotNull Context context) {
        log.debug("request - weightRecordsByDayRange");
        int days = Integer.parseInt(StringUtils.defaultIfBlank(context.queryParam("days"), "10"));
        log.debug("days - {}", days);
        context.json(healthService.weightRecordsByDayRange(days))
                .status(HttpStatus.OK);
    }

    private static void addWeightRecord(@NotNull Context context) {
        log.debug("request - addWeightRecord");
        WeightRecord weightRecord = context.bodyAsClass(WeightRecord.class);
        log.debug("weightRecord - {}", weightRecord);
        context.json(new MessageResponse(healthService.addWeightRecord(weightRecord).getId().getId().getLong() + ""))
                .status(HttpStatus.CREATED);
    }

    private static void allWeightRecords(@NotNull Context context) {
        log.debug("request - allWeightRecords");
        context.json(healthService.allWeightRecords())
                .status(HttpStatus.OK);
    }
}