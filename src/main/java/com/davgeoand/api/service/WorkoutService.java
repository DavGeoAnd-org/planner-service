package com.davgeoand.api.service;

import com.davgeoand.api.data.WorkoutDB;
import com.davgeoand.api.exception.WorkoutException;
import com.davgeoand.api.model.workout.Exercise;
import com.davgeoand.api.model.workout.Workout;
import com.davgeoand.api.model.workout.WorkoutDetail;
import com.davgeoand.api.model.workout.WorkoutStep;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@Slf4j
@NoArgsConstructor
public class WorkoutService {
    private final WorkoutDB workoutDB = new WorkoutDB();

    public List<Exercise> allExercises() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(workoutDB.selectAllExercises(), Spliterator.ORDERED), false).toList();
    }

    public Exercise addExercise(Exercise exercise) {
        log.debug("exercise - {}", exercise);
        return workoutDB.insertExercise(exercise);
    }

    public List<Workout> allWorkouts() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(workoutDB.selectAllWorkouts(), Spliterator.ORDERED), false).toList();
    }

    public Workout addWorkout(WorkoutDetail workoutDetail) {
        log.debug("workoutDetail - {}", workoutDetail);
        Workout workout = workoutDB.insertWorkout(new Workout(null, workoutDetail.getName(), workoutDetail.getWhen()));
        log.debug("workout - {}", workout);
        workoutDetail.getSteps().forEach(step -> {
            log.debug("step - {}", step);
            workoutDB.insertWorkoutStep(new WorkoutStep(null, workout.getId(), step.getExercise().getId(), step.getOrder(), step.getNote()));
        });
        return workout;
    }

    private Exercise exercise(String id) {
        log.debug("id - {}", id);
        return workoutDB.selectExercise(id).orElseThrow(() -> new WorkoutException.MissingExerciseException(id));
    }

    public WorkoutDetail workoutDetail(String id) {
        log.debug("id - {}", id);
        Workout workout = workout(id);
        log.debug("workout - {}", workout);
        return workoutDB.selectWorkoutDetail(workout.getId());
    }

    private Workout workout(String id) {
        log.debug("id - {}", id);
        return workoutDB.selectWorkout(id).orElseThrow(() -> new WorkoutException.MissingWorkoutException(id));
    }
}
