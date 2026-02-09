package com.davgeoand.api.controller;

import com.davgeoand.api.service.WorkoutService;
import io.javalin.apibuilder.EndpointGroup;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import static io.javalin.apibuilder.ApiBuilder.path;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkoutController {
    public final static WorkoutService workoutService = new WorkoutService();

    public static @NotNull EndpointGroup getWorkoutEndpoints() {
        return () -> {
            path("exercises", () -> {
                path("{id}", () -> {
                });
            });
            path("workouts", () -> {
                path("{id}", () -> {
                });
            });
        };
    }
}
