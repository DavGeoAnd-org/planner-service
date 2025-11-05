package com.davgeoand.api.controller;

import com.davgeoand.api.model.weight.WeightRecord;
import com.davgeoand.api.service.WeightService;
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
public class WeightController {
    public final static WeightService weightService = new WeightService();

    public static @NotNull EndpointGroup getWeightEndpoints() {
        return () -> path("weightRecords", () -> {
            get(WeightController::allWeightRecords);
            post(WeightController::addWeightRecord);
        });
    }

    private static void addWeightRecord(@NotNull Context context) {
        WeightRecord weightRecord = context.bodyAsClass(WeightRecord.class);
        context.json("Added weight on: " + weightService.addWeightRecord(weightRecord).getTimestamp());
        context.status(HttpStatus.CREATED);
    }

    private static void allWeightRecords(@NotNull Context context) {
        context.json(weightService.allWeightRecords());
        context.status(HttpStatus.OK);
    }
}