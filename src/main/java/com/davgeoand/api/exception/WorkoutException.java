package com.davgeoand.api.exception;

public class WorkoutException {
    public static class MissingException extends NullPointerException {
        public MissingException(String string) {
            super(string);
        }
    }

    public static class MissingExerciseException extends WorkoutException.MissingException {
        public MissingExerciseException(String exerciseId) {
            super("Exercise does not exist: " + exerciseId);
        }
    }

    public static class MissingWorkoutException extends WorkoutException.MissingException {
        public MissingWorkoutException(String workoutId) {
            super("Workout does not exist: " + workoutId);
        }

    }
}
