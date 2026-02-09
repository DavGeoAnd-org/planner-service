package com.davgeoand.api;

import com.davgeoand.api.controller.AdminController;
import com.davgeoand.api.controller.GroceryController;
import com.davgeoand.api.controller.HealthController;
import com.davgeoand.api.controller.WorkoutController;
import com.davgeoand.api.exception.GroceryException;
import com.davgeoand.api.exception.JavalinServiceException.MissingPropertyException;
import com.davgeoand.api.exception.WorkoutException;
import com.surrealdb.SurrealException;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.HttpStatus;
import io.javalin.micrometer.MicrometerPlugin;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

import static io.javalin.apibuilder.ApiBuilder.path;

@Slf4j
public class JavalinService {
    private final Javalin javalin;
    private final String SERVICE_NAME = ServiceProperties.getProperty("service.name").orElseThrow(() -> new MissingPropertyException("service.name"));
    private final String SERVICE_CONTEXT_PATH = ServiceProperties.getProperty("service.context.path").orElseThrow(() -> new MissingPropertyException("service.context.path"));
    private final String SERVICE_PORT = ServiceProperties.getProperty("service.port").orElseThrow(() -> new MissingPropertyException("service.port"));

    public JavalinService() {
        log.info("Initializing {}", SERVICE_NAME);
        javalin = Javalin.create(javalinConfig -> {
            javalinConfig.router.apiBuilder(routes());
            javalinConfig.router.contextPath = SERVICE_CONTEXT_PATH;
            javalinConfig.registerPlugin(micrometerRegistry());
        });
        addExceptionHandlers();
        log.info("Initialized {}", SERVICE_NAME);
    }

    @WithSpan
    private MicrometerPlugin micrometerRegistry() {
        log.info("Adding micrometer registry");
        return new MicrometerPlugin(
                micrometerPluginConfig -> micrometerPluginConfig.registry = ServiceMeterRegistry.meterRegistry);
    }

    @WithSpan
    private void addExceptionHandlers() {
        log.info("Adding exception handlers");
        javalin.exception(GroceryException.MissingException.class, (e, context) -> {
            context.result(e.getMessage());
            context.status(HttpStatus.NOT_FOUND);
        });
        javalin.exception(SurrealException.class, (e, context) -> {
            context.result(e.getMessage());
            context.status(HttpStatus.INTERNAL_SERVER_ERROR);
        });
        javalin.exception(WorkoutException.MissingException.class, (e, context) -> {
            context.result(e.getMessage());
            context.status(HttpStatus.NOT_FOUND);
        });
        log.info("Added exception handlers");
    }

    @WithSpan
    private EndpointGroup routes() {
        log.info("Adding routes");
        return () -> {
            path("admin", AdminController.getAdminEndpoints());
            path("grocery", GroceryController.getGroceryEndpoints());
            path("health", HealthController.getHealthEndpoints());
            path("workout", WorkoutController.getWorkoutEndpoints());
        };
    }

    public void start() {
        log.info("Starting {}", SERVICE_NAME);
        javalin.start(Integer.parseInt(SERVICE_PORT));
        log.info("Started {}", SERVICE_NAME);
    }
}