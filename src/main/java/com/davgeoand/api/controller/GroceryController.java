package com.davgeoand.api.controller;

import com.davgeoand.api.model.grocery.*;
import com.davgeoand.api.service.GroceryService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import static io.javalin.apibuilder.ApiBuilder.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroceryController {
    public static @NotNull EndpointGroup getGroceryEndpoints() {
        return () -> {
            path("items", () -> {
                get(GroceryController::allItems);
                post(GroceryController::createItem);
                path("{id}", () -> {
                    get(GroceryController::item);
                    get("categories", GroceryController::allCategoriesForItem);
                    get("stores", GroceryController::allStoresForItem);
                    path("category/{categoryId}", () -> post(GroceryController::createProductOf));
                    path("store/{storeId}", () -> post(GroceryController::createSoldAt));
                });
            });
            path("categories", () -> {
                get(GroceryController::allCategories);
                post(GroceryController::createCategory);
                path("{id}", () -> {
                    get(GroceryController::category);
                    get("items", GroceryController::allItemsForCategory);
                });
            });
            path("stores", () -> {
                get(GroceryController::allStores);
                post(GroceryController::createStore);
                path("{id}", () -> {
                    get(GroceryController::store);
                    get("items", GroceryController::allItemsForStore);
                    path("list", () -> {
                        get(GroceryController::storeList);
                        put(GroceryController::updateStoreList);
                    });
                });
            });
        };
    }

    private static void storeList(@NotNull Context context) {
        String id = context.pathParam("id");
        context.json(GroceryService.storeList(id));
        context.status(HttpStatus.OK);
    }

    private static void updateStoreList(@NotNull Context context) {
        String id = context.pathParam("id");
        ListUpdate listUpdate = context.bodyAsClass(ListUpdate.class);
        GroceryService.updateList(id, listUpdate);
        context.status(HttpStatus.ACCEPTED);
    }

    private static void allItemsForStore(@NotNull Context context) {
        String id = context.pathParam("id");
        context.json(GroceryService.allItemsForStore(id));
        context.status(HttpStatus.OK);
    }

    private static void createSoldAt(@NotNull Context context) {
        String itemId = context.pathParam("id");
        String storeId = context.pathParam("storeId");
        SoldAt soldAt = context.bodyAsClass(SoldAt.class);
        context.json("Created: " + GroceryService.createSoldAt(itemId, storeId, soldAt).getId());
        context.status(HttpStatus.CREATED);
    }

    private static void allStoresForItem(@NotNull Context context) {
        String id = context.pathParam("id");
        String levelQueryParam = StringUtils.defaultIfBlank(context.queryParam("location"), "false");
        if (levelQueryParam.equals("true")) {
            context.json(GroceryService.allStoresForItemWithLocation(id));
        } else {
            context.json(GroceryService.allStoresForItem(id));
        }
        context.status(HttpStatus.OK);
    }

    private static void allItemsForCategory(@NotNull Context context) {
        String id = context.pathParam("id");
        context.json(GroceryService.allItemsForCategory(id));
        context.status(HttpStatus.OK);
    }

    private static void createProductOf(@NotNull Context context) {
        String itemId = context.pathParam("id");
        String categoryId = context.pathParam("categoryId");
        context.json("Created: " + GroceryService.createProductOf(itemId, categoryId).getId());
        context.status(HttpStatus.CREATED);
    }

    private static void allCategoriesForItem(@NotNull Context context) {
        String id = context.pathParam("id");
        context.json(GroceryService.allCategoriesForItem(id));
        context.status(HttpStatus.OK);
    }

    private static void store(@NotNull Context context) {
        String id = context.pathParam("id");
        context.json(GroceryService.store(id));
        context.status(HttpStatus.FOUND);
    }

    private static void category(@NotNull Context context) {
        String id = context.pathParam("id");
        context.json(GroceryService.category(id));
        context.status(HttpStatus.FOUND);
    }

    private static void item(@NotNull Context context) {
        String id = context.pathParam("id");
        context.json(GroceryService.item(id));
        context.status(HttpStatus.FOUND);
    }

    private static void createStore(@NotNull Context context) {
        Store store = context.bodyAsClass(Store.class);
        context.json("Created: " + GroceryService.createStore(store).getId());
        context.status(HttpStatus.CREATED);
    }

    private static void createCategory(@NotNull Context context) {
        Category category = context.bodyAsClass(Category.class);
        context.json("Created: " + GroceryService.createCategory(category).getId());
        context.status(HttpStatus.CREATED);
    }

    private static void createItem(@NotNull Context context) {
        Item item = context.bodyAsClass(Item.class);
        context.json("Created: " + GroceryService.createItem(item).getId());
        context.status(HttpStatus.CREATED);
    }

    private static void allStores(@NotNull Context context) {
        context.json(GroceryService.allStores());
        context.status(HttpStatus.OK);
    }

    private static void allCategories(@NotNull Context context) {
        context.json(GroceryService.allCategories());
        context.status(HttpStatus.OK);
    }

    private static void allItems(@NotNull Context context) {
        context.json(GroceryService.allItems());
        context.status(HttpStatus.OK);
    }
}
