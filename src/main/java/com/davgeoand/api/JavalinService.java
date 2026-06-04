package com.davgeoand.api;

import com.davgeoand.api.controller.AdminController;
import com.davgeoand.api.controller.GroceryController;
import com.davgeoand.api.controller.HealthController;
import com.davgeoand.api.controller.WorkoutController;
import com.davgeoand.api.exception.GroceryException;
import com.davgeoand.api.exception.WorkoutException;
import com.davgeoand.api.monitor.event.ServiceEventHandler;
import com.davgeoand.api.monitor.metric.ServiceMeterRegistry;
import com.surrealdb.SurrealException;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;
import io.javalin.event.LifecycleEventListener;
import io.javalin.http.HttpStatus;
import io.javalin.micrometer.MicrometerPlugin;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

import static io.javalin.apibuilder.ApiBuilder.path;

@Slf4j
public class JavalinService {
    private final Javalin javalin;
    private final String SERVICE_NAME = ServiceProperties.getProperty("service.name");

    public JavalinService() {
        log.info("Initializing {}", SERVICE_NAME);
        javalin = Javalin.create(javalinConfig -> {
            javalinConfig.routes.apiBuilder(routes());
            javalinConfig.router.contextPath = ServiceProperties.getProperty("context.path");
            javalinConfig.registerPlugin(serviceMeterRegistry());
            javalinConfig.events.serverStarted(serverStartedEvents());
            javalinConfig.startup.showJavalinBanner = false;
            javalinConfig.startup.showOldJavalinVersionWarning = false;
            exceptionHandlers(javalinConfig);
        });
        ServiceEventHandler.init();
        log.info("Finished initializing {}", SERVICE_NAME);
    }

    @WithSpan
    private void exceptionHandlers(JavalinConfig javalinConfig) {
        log.info("Adding exception handlers");
        javalinConfig.routes.exception(SurrealException.class, (e, context) -> {
            context.result(e.getMessage());
            context.status(HttpStatus.INTERNAL_SERVER_ERROR);
        });
        javalinConfig.routes.exception(GroceryException.MissingException.class, (e, context) -> {
            context.result(e.getMessage());
            context.status(HttpStatus.NOT_FOUND);
        });
        javalinConfig.routes.exception(WorkoutException.MissingException.class, (e, context) -> {
            context.result(e.getMessage());
            context.status(HttpStatus.NOT_FOUND);
        });
        log.info("Added exception handlers");
    }

    @WithSpan
    private LifecycleEventListener serverStartedEvents() {
        log.info("Adding server started events");
        return AdminController::addServiceInfo;
    }

    @WithSpan
    private MicrometerPlugin serviceMeterRegistry() {
        log.info("Adding service meter registry");
        return new MicrometerPlugin(micrometerPluginConfig -> micrometerPluginConfig.registry = ServiceMeterRegistry.getMeterRegistry());
    }

    @WithSpan
    private EndpointGroup routes() {
        log.info("Adding routes");
        return () -> {
            path("admin", AdminController.getAdminEndpoints());
            path("health", HealthController.getHealthEndpoints());
            path("grocery", GroceryController.getGroceryEndpoints());
            path("workout", WorkoutController.getWorkoutEndpoints());
        };
    }

    public void start() {
        log.info("Starting {}", SERVICE_NAME);
        javalin.start(Integer.parseInt(ServiceProperties.getProperty("port")));
        log.info("Started {}", SERVICE_NAME);
    }
}