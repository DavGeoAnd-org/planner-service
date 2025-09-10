package com.davgeoand.api;

import com.davgeoand.api.controller.AdminController;
import com.davgeoand.api.exception.JavalinServiceException;
import com.davgeoand.api.helper.Constants;
import com.davgeoand.api.helper.ServiceProperties;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.event.LifecycleEventListener;
import lombok.extern.slf4j.Slf4j;

import static io.javalin.apibuilder.ApiBuilder.path;

@Slf4j
public class JavalinService {
    private final Javalin javalin;
    private final String SERVICE_NAME = ServiceProperties.getProperty(Constants.SERVICE_NAME)
            .orElseThrow(() -> new JavalinServiceException.MissingPropertyException(Constants.SERVICE_NAME));
    private final String SERVICE_CONTEXT_PATH = ServiceProperties.getProperty(Constants.SERVICE_CONTEXT_PATH)
            .orElseThrow(() -> new JavalinServiceException.MissingPropertyException(Constants.SERVICE_CONTEXT_PATH));
    private final String SERVICE_PORT = ServiceProperties.getProperty(Constants.SERVICE_PORT)
            .orElseThrow(() -> new JavalinServiceException.MissingPropertyException(Constants.SERVICE_PORT));

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
        log.info("Added exception handlers");
    }

    private LifecycleEventListener eventServerStarting() {
        return () -> {
            log.info("Starting steps for serverStarting event");
            log.info("Finished steps for serverStarting event");
        };
    }

    private EndpointGroup routes() {
        return () -> {
            path("admin", AdminController.getAdminEndpoints());
        };
    }

    public void start() {
        log.info("Starting {}", SERVICE_NAME);
        javalin.start(Integer.parseInt(SERVICE_PORT));
        log.info("Started {}", SERVICE_NAME);
    }
}