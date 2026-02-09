package com.davgeoand.api.controller;

import com.davgeoand.api.model.response.MessageResponse;
import com.davgeoand.api.service.AdminService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import static io.javalin.apibuilder.ApiBuilder.get;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminController {
    private final static AdminService adminService = new AdminService();

    public static @NotNull EndpointGroup getAdminEndpoints() {
        return () -> {
            get("health", AdminController::health);
            get("metrics", AdminController::metrics);
        };
    }

    private static void metrics(@NotNull Context context) {
        log.debug("request - metrics");
        context.json(adminService.metrics())
                .status(HttpStatus.OK);
    }

    private static void health(@NotNull Context context) {
        log.debug("request - health");
        context.json(new MessageResponse(adminService.health()))
                .status(HttpStatus.OK);
    }
}