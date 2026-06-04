package com.davgeoand.api.exception;

public class ServiceException {
    public static class MissingPropertyException extends NullPointerException {
        public MissingPropertyException(String property) {
            super("Missing property: " + property);
        }
    }
}