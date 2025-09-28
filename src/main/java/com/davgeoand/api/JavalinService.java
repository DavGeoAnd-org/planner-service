package com.davgeoand.api;

import com.davgeoand.api.controller.AdminController;
import com.davgeoand.api.controller.GroceryController;
import com.davgeoand.api.data.GroceryDB;
import com.davgeoand.api.exception.GroceryException;
import com.davgeoand.api.exception.JavalinServiceException;
import com.davgeoand.api.helper.Constants;
import com.davgeoand.api.helper.ServiceProperties;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.event.LifecycleEventListener;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import static io.javalin.apibuilder.ApiBuilder.path;

@Slf4j
public class JavalinService {
    private final Javalin javalin;
    private final String SERVICE_NAME = ServiceProperties.getProperty(Constants.SERVICE_NAME)
            .orElseThrow(() -> new JavalinServiceException.MissingPropertyException(Constants.SERVICE_NAME));
    private final String SERVICE_PORT = ServiceProperties.getProperty(Constants.SERVICE_PORT)
            .orElseThrow(() -> new JavalinServiceException.MissingPropertyException(Constants.SERVICE_PORT));
    private final String SERVICE_CONTEXT_PATH = ServiceProperties.getProperty(Constants.SERVICE_CONTEXT_PATH)
            .orElseThrow(() -> new JavalinServiceException.MissingPropertyException(Constants.SERVICE_CONTEXT_PATH));

    public JavalinService() {
        log.info("Initializing {}", SERVICE_NAME);
        javalin = Javalin.create(javalinConfig -> {
            javalinConfig.router.apiBuilder(routes());
            javalinConfig.router.contextPath = SERVICE_CONTEXT_PATH;
            javalinConfig.events.serverStarting(eventServerStarting());
        });
        addExceptionHandlers();
        log.info("Initialized {}", SERVICE_NAME);
    }

    private void addExceptionHandlers() {
        log.info("Adding exception handlers");
        javalin.exception(GroceryException.MissingException.class, (e, context) -> {
            context.result(e.getMessage());
            context.status(HttpStatus.NOT_FOUND);
        });
        log.info("Added exception handlers");
    }

    private LifecycleEventListener eventServerStarting() {
        return () -> {
            log.info("Starting steps for serverStarting event");
            GroceryDB.init();
            log.info("Finished steps for serverStarting event");
        };
    }

    private EndpointGroup routes() {
        return () -> {
            path("admin", AdminController.getAdminEndpoints());
            path("grocery", GroceryController.getGroceryEndpoints());
        };
    }

    public void start() {
        log.info("Starting {}", SERVICE_NAME);
        javalin.start(Integer.parseInt(SERVICE_PORT));
        log.info("Started {}", SERVICE_NAME);
    }
}