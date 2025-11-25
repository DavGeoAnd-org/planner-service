package com.davgeoand.api.controller;

import com.davgeoand.api.model.grocery.category.Category;
import com.davgeoand.api.model.grocery.item.ItemFullDetail;
import com.davgeoand.api.model.grocery.store.Store;
import com.davgeoand.api.model.response.RecordIdResponse;
import com.davgeoand.api.service.GroceryService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import static io.javalin.apibuilder.ApiBuilder.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroceryController {
    public final static GroceryService groceryService = new GroceryService();

    public static @NotNull EndpointGroup getGroceryEndpoints() {
        return () -> {
            path("stores", () -> {
                get(GroceryController::allStores);
                post(GroceryController::createStore);
                path("{id}", () -> {
                    get("list", GroceryController::storeList);
                });
            });
            path("categories", () -> {
                get(GroceryController::allCategories);
                post(GroceryController::createCategory);
                path("{categoryId}", () -> {
                    path("stores/list", () -> {
                        post(GroceryController::addCategoryToLists);
                        delete(GroceryController::removeCategoryFromLists);
                    });
                });
            });
            path("items", () -> {
                get(GroceryController::allItems);
                post(GroceryController::createItem);
            });
        };
    }

    private static void removeCategoryFromLists(@NotNull Context context) {
        log.debug("request - removeCategoryFromLists");
        String categoryId = context.pathParam("categoryId");
        log.debug("categoryId - {}", categoryId);
        context.json(groceryService.removeCategoryToLists(categoryId).stream()
                .map(storeList -> storeList.getIn().getId().getString()).toList())
                .status(HttpStatus.OK);
    }

    private static void storeList(@NotNull Context context) {
        log.debug("request - storeList");
        String id = context.pathParam("id");
        log.debug("id - {}", id);
        context.json(groceryService.storeList(id))
                .status(HttpStatus.OK);
    }

    private static void addCategoryToLists(@NotNull Context context) {
        log.debug("request - addCategoryToLists");
        String categoryId = context.pathParam("categoryId");
        log.debug("categoryId - {}", categoryId);
        context.json(groceryService.addCategoryToLists(categoryId).stream()
                .map(storeList -> storeList.getIn().getId().getString()).toList())
                .status(HttpStatus.CREATED);
    }

    private static void createItem(@NotNull Context context) {
        log.debug("request - createItem");
        ItemFullDetail itemFullDetail = context.bodyAsClass(ItemFullDetail.class);
        log.debug("item - {}", itemFullDetail);
        context.json(
                new RecordIdResponse("Created Item",
                        groceryService.createItem(itemFullDetail).getId()))
                .status(HttpStatus.CREATED);
    }

    private static void allItems(@NotNull Context context) {
        log.debug("request - allItems");
        context.json(groceryService.allItems())
                .status(HttpStatus.OK);
    }

    private static void createCategory(@NotNull Context context) {
        log.debug("request - createCategory");
        Category category = context.bodyAsClass(Category.class);
        log.debug("category - {}", category);
        context.json(
                new RecordIdResponse("Created Category",
                        groceryService.createCategory(category).getId()))
                .status(HttpStatus.CREATED);
    }

    private static void allCategories(@NotNull Context context) {
        log.debug("request - allCategories");
        context.json(groceryService.allCategories())
                .status(HttpStatus.OK);
    }

    private static void createStore(@NotNull Context context) {
        log.debug("request - createStore");
        Store store = context.bodyAsClass(Store.class);
        log.debug("store - {}", store);
        context.json(
                new RecordIdResponse("Created Store",
                        groceryService.createStore(store).getId()))
                .status(HttpStatus.CREATED);
    }

    private static void allStores(@NotNull Context context) {
        log.debug("request - allStores");
        context.json(groceryService.allStores())
                .status(HttpStatus.OK);
    }
}
