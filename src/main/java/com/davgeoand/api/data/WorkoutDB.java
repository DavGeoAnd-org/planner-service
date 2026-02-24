package com.davgeoand.api.data;

import com.davgeoand.api.ServiceProperties;
import com.davgeoand.api.exception.JavalinServiceException;
import com.davgeoand.api.model.workout.Exercise;
import com.davgeoand.api.model.workout.Workout;
import com.davgeoand.api.model.workout.WorkoutDetail;
import com.davgeoand.api.model.workout.WorkoutStep;
import com.surrealdb.*;
import com.surrealdb.signin.Root;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

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
                .useDb("workout")
                .signin(new Root(SURREALDB_USERNAME, SURREALDB_PASSWORD));
        log.info("Connected to workout db");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Exercise> selectAllExercises() {
        return driver.select(Exercise.class, "exercises");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Exercise insertExercise(Exercise exercise) {
        log.debug("exercise - {}", exercise);
        return driver.create(Exercise.class, new RecordId("exercises", exercise.getName()), exercise);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Workout> selectAllWorkouts() {
        return driver.select(Workout.class, "workouts");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Workout insertWorkout(Workout workout) {
        log.debug("workout - {}", workout);
        return driver.create(Workout.class, new RecordId("workouts", workout.getName()), workout);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Optional<Exercise> selectExercise(String id) {
        log.debug("id - {}", id);
        return driver.select(Exercise.class, new RecordId("exercises", id));
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public void insertWorkoutStep(WorkoutStep workoutStep) {
        log.debug("workoutStep - {}", workoutStep);
        driver.relate(workoutStep.getIn(), "workout_steps", workoutStep.getOut(), workoutStep);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Optional<Workout> selectWorkout(String id) {
        log.debug("id - {}", id);
        return driver.select(Workout.class, new RecordId("workouts", id));
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public WorkoutDetail selectWorkoutDetail(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind(
                """
                        SELECT *,
                            (SELECT order, note,
                                (SELECT * FROM ONLY $parent.out) AS exercise
                            FROM $parent->workout_steps) AS steps
                        FROM ONLY $id;""",
                Map.of("id", id));
        return response.take(0).get(WorkoutDetail.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Exercise updateExercise(Exercise exercise) {
        log.debug("exercise - {}", exercise);
        return driver.update(Exercise.class, exercise.getId(), UpType.CONTENT, exercise);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Workout updateWorkout(Workout workout) {
        log.debug("workout - {}", workout);
        return driver.update(Workout.class, workout.getId(), UpType.CONTENT, workout);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public void removeWorkoutStepsFromWorkout(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind(
                "DELETE workout_steps WHERE in = $id RETURN BEFORE;",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        results.iterator(WorkoutStep.class);
    }
}
