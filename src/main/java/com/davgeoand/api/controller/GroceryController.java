package com.davgeoand.api.controller;

import com.davgeoand.api.service.GroceryService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroceryController {
    public static @NotNull EndpointGroup getGroceryEndpoints() {
        return () -> {
            path("items", () -> {
                get(GroceryController::getAllItemsRequest);
                path("{id}", () -> {
                    get(GroceryController::getItemByIdRequest);
                    get("categories", GroceryController::getAllCategoriesForItemByItemIdRequest);
                    get("stores", GroceryController::getAllStoresForItemByItemIdRequest);
                });
            });
            path("categories", () -> {
                get(GroceryController::getAllCategoriesRequest);
                path("{id}", () -> {
                    get(GroceryController::getCategoryByIdRequest);
                    get("items", GroceryController::getAllItemsForCategoryByCategoryIdRequest);
                });
            });
            path("stores", () -> {
                get(GroceryController::getAllStoresRequest);
                path("{id}", () -> {
                    get(GroceryController::getStoreByIdRequest);
                    get("items", GroceryController::getAllItemsForStoreByStoreIdRequest);
                });
            });
        };
    }

    private static void getAllItemsForStoreByStoreIdRequest(@NotNull Context context) {
        String id = context.pathParam("id");
        context.json(GroceryService.allItemsForStoreByStoreId(id));
        context.status(HttpStatus.OK);
    }

    private static void getCategoryByIdRequest(@NotNull Context context) {
        String id = context.pathParam("id");
        context.json(GroceryService.categoryById(id));
        context.status(HttpStatus.FOUND);
    }

    private static void getAllItemsRequest(@NotNull Context context) {
        context.json(GroceryService.allItems());
        context.status(HttpStatus.OK);
    }

    private static void getItemByIdRequest(@NotNull Context context) {
        String id = context.pathParam("id");
        context.json(GroceryService.itemById(id));
        context.status(HttpStatus.FOUND);
    }

    private static void getAllCategoriesRequest(@NotNull Context context) {
        context.json(GroceryService.allCategories());
        context.status(HttpStatus.OK);
    }

    private static void getAllStoresRequest(@NotNull Context context) {
        context.json(GroceryService.allStores());
        context.status(HttpStatus.OK);
    }

    private static void getAllCategoriesForItemByItemIdRequest(@NotNull Context context) {
        String id = context.pathParam("id");
        context.json(GroceryService.allCategoriesForItemByItemId(id));
        context.status(HttpStatus.OK);
    }

    private static void getAllItemsForCategoryByCategoryIdRequest(@NotNull Context context) {
        String id = context.pathParam("id");
        context.json(GroceryService.allItemsForCategoryByCategoryId(id));
        context.status(HttpStatus.OK);
    }

    private static void getStoreByIdRequest(@NotNull Context context) {
        String id = context.pathParam("id");
        context.json(GroceryService.storeById(id));
        context.status(HttpStatus.FOUND);
    }

    private static void getAllStoresForItemByItemIdRequest(@NotNull Context context) {
        String id = context.pathParam("id");
        context.json(GroceryService.allStoresForItemByItemId(id));
        context.status(HttpStatus.OK);
    }

}
