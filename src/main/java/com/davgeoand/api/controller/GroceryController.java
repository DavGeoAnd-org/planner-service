package com.davgeoand.api.controller;

import com.davgeoand.api.model.grocery.Category;
import com.davgeoand.api.model.grocery.Item;
import com.davgeoand.api.model.grocery.SoldAt;
import com.davgeoand.api.model.grocery.Store;
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
    public static @NotNull EndpointGroup getGroceryEndpoints() {
        return () -> {
            path("items", () -> {
                get(GroceryController::getAllItemsRequest);
                post(GroceryController::postAddItemRequest);
                path("{id}", () -> {
                    get(GroceryController::getItemByIdRequest);
                    get("categories", GroceryController::getAllCategoriesForItemByItemIdRequest);
                    get("stores", GroceryController::getAllStoresForItemByItemIdRequest);
                    put(GroceryController::putUpdateItemRequest);
                    path("category/{categoryId}", () -> {
                        post(GroceryController::postAddProductOfRequest);
                    });
                    path("store/{storeId}", () -> {
                        post(GroceryController::postAddSoldAtRequest);
                    });
                });
            });
            path("categories", () -> {
                get(GroceryController::getAllCategoriesRequest);
                post(GroceryController::postAddCategoryRequest);
                path("{id}", () -> {
                    get(GroceryController::getCategoryByIdRequest);
                    get("items", GroceryController::getAllItemsForCategoryByCategoryIdRequest);
                });
            });
            path("stores", () -> {
                get(GroceryController::getAllStoresRequest);
                post(GroceryController::postAddStoreRequest);
                path("{id}", () -> {
                    get(GroceryController::getStoreByIdRequest);
                    get("items", GroceryController::getAllItemsForStoreByStoreIdRequest);
                });
            });
        };
    }

    private static void postAddSoldAtRequest(@NotNull Context context) {
        String itemId = context.pathParam("id");
        String storeId = context.pathParam("storeId");
        SoldAt soldAt = context.bodyAsClass(SoldAt.class);
        context.json("Created: " + GroceryService.addSoldAt(itemId, storeId, soldAt).getId());
        context.status(HttpStatus.ACCEPTED);
    }

    private static void postAddProductOfRequest(@NotNull Context context) {
        String itemId = context.pathParam("id");
        String categoryId = context.pathParam("categoryId");
        context.json("Created: " + GroceryService.addProductOf(itemId, categoryId).getId());
        context.status(HttpStatus.ACCEPTED);
    }

    private static void postAddStoreRequest(@NotNull Context context) {
        Store store = context.bodyAsClass(Store.class);
        context.json("Created: " + GroceryService.addStore(store).getId());
        context.status(HttpStatus.ACCEPTED);
    }

    private static void postAddCategoryRequest(@NotNull Context context) {
        Category category = context.bodyAsClass(Category.class);
        context.json("Created: " + GroceryService.addCategory(category).getId());
        context.status(HttpStatus.ACCEPTED);
    }

    private static void putUpdateItemRequest(@NotNull Context context) {
        String id = context.pathParam("id");
        Item item = context.bodyAsClass(Item.class);
        context.json("Updated: " + GroceryService.updateItem(id, item).getId());
    }

    private static void postAddItemRequest(@NotNull Context context) {
        Item item = context.bodyAsClass(Item.class);
        context.json("Created: " + GroceryService.addItem(item).getId());
        context.status(HttpStatus.ACCEPTED);
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
