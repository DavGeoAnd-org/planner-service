package com.davgeoand.api;

import com.davgeoand.api.controller.AdminController;
import com.davgeoand.api.controller.GroceryController;
import com.davgeoand.api.controller.WeightController;
import com.davgeoand.api.exception.GroceryException;
import com.surrealdb.SurrealException;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import static io.javalin.apibuilder.ApiBuilder.path;

@Slf4j
public class JavalinService {
    private final Javalin javalin;
    private final String SERVICE_NAME = Constants.SERVICE_NAME;

    public JavalinService() {
        log.info("Initializing {}", SERVICE_NAME);
        javalin = Javalin.create(javalinConfig -> {
            javalinConfig.router.apiBuilder(routes());
            javalinConfig.router.contextPath = Constants.SERVICE_CONTEXT_PATH;
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
        javalin.exception(SurrealException.class, (e, context) -> {
            context.result(e.getMessage());
            context.status(HttpStatus.INTERNAL_SERVER_ERROR);
        });
        log.info("Added exception handlers");
    }

    private EndpointGroup routes() {
        return () -> {
            path("admin", AdminController.getAdminEndpoints());
            path("grocery", GroceryController.getGroceryEndpoints());
            path("weight", WeightController.getWeightEndpoints());
        };
    }

    public void start() {
        log.info("Starting {}", SERVICE_NAME);
        javalin.start(Integer.parseInt(Constants.SERVICE_PORT));
        log.info("Started {}", SERVICE_NAME);
    }
}