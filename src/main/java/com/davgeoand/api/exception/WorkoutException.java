package com.davgeoand.api.exception;

public class WorkoutException {
    public static class MissingException extends NullPointerException {
        public MissingException(String string) {
            super(string);
        }
    }

    public static class MissingExerciseException extends WorkoutException.MissingException {
        public MissingExerciseException(String categoryId) {
            super("Exercise does not exist: " + categoryId);
        }
    }
}
