package com.davgeoand.api.controller;

import com.davgeoand.api.model.response.RecordIdResponse;
import com.davgeoand.api.model.workout.Exercise;
import com.davgeoand.api.model.workout.WorkoutDetail;
import com.davgeoand.api.service.WorkoutService;
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
public class WorkoutController {
    public final static WorkoutService workoutService = new WorkoutService();

    public static @NotNull EndpointGroup getWorkoutEndpoints() {
        return () -> {
            path("exercises", () -> {
                get(WorkoutController::allExercises);
                post(WorkoutController::addExercise);
                path("{id}", () -> {
                });
            });
            path("workouts", () -> {
                get(WorkoutController::allWorkouts);
                post(WorkoutController::addWorkout);
                path("{id}", () -> {
                    get(WorkoutController::workoutDetail);
                });
            });
        };
    }

    private static void workoutDetail(@NotNull Context context) {
        log.debug("request - workout");
        String id = context.pathParam("id");
        context.json(workoutService.workoutDetail(id))
                .status(HttpStatus.OK);
    }

    private static void addWorkout(@NotNull Context context) {
        log.debug("request - addWorkout");
        WorkoutDetail workoutDetail = context.bodyAsClass(WorkoutDetail.class);
        log.debug("workoutDetail - {}", workoutDetail);
        context.json(new RecordIdResponse("Added Workout",
                        workoutService.addWorkout(workoutDetail).getId()))
                .status(HttpStatus.CREATED);
    }

    private static void allWorkouts(@NotNull Context context) {
        log.debug("request - allWorkouts");
        context.json(workoutService.allWorkouts())
                .status(HttpStatus.OK);
    }

    private static void addExercise(@NotNull Context context) {
        log.debug("request - addExercise");
        Exercise exercise = context.bodyAsClass(Exercise.class);
        log.debug("exercise - {}", exercise);
        context.json(new RecordIdResponse("Added Exercise",
                        workoutService.addExercise(exercise).getId()))
                .status(HttpStatus.CREATED);
    }

    private static void allExercises(@NotNull Context context) {
        log.debug("request - allExercises");
        context.json(workoutService.allExercises())
                .status(HttpStatus.OK);
    }
}
