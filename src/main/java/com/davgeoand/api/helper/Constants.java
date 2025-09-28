package com.davgeoand.api.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    // Service
    public static final String SERVICE_NAME = "service.name";
    public static final String SERVICE_PORT = "service.port";
    public static final String SERVICE_CONTEXT_PATH = "service.context_path";
    // SurrealDB
    public static final String SURREALDB_CONNECT = "surrealdb.connect";
    public static final String SURREALDB_NAMESPACE = "surrealdb.namespace";
    public static final String SURREALDB_USERNAME = "surrealdb.username";
    public static final String SURREALDB_PASSWORD = "surrealdb.password";
}