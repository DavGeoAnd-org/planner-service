package com.davgeoand.api.controller;

import com.davgeoand.api.model.grocery.category.Category;
import com.davgeoand.api.model.grocery.item.Item;
import com.davgeoand.api.model.grocery.item.ItemDetail;
import com.davgeoand.api.model.grocery.store.Store;
import com.davgeoand.api.model.response.RecordIdResponse;
import com.davgeoand.api.service.GroceryService;
import com.surrealdb.RecordId;
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
    public final static GroceryService groceryService = new GroceryService();

    public static @NotNull EndpointGroup getGroceryEndpoints() {
        return () -> {
            path("stores", () -> {
                get(GroceryController::allStores);
                post(GroceryController::addStore);
                path("{storeId}", () -> {
                    get(GroceryController::store);
                    delete(GroceryController::removeStore);
                    get("list/{categoryId}", GroceryController::getItemsForCategoryAtStore);
                });
            });
            path("categories", () -> {
                get(GroceryController::allCategories);
                post(GroceryController::addCategory);
                path("{categoryId}", () -> {
                    get(GroceryController::category);
                    delete(GroceryController::removeCategory);
                    path("storeList", () -> {
                        post(GroceryController::addCategoryToStoreLists);
                        delete(GroceryController::removeCategoryFromStoreLists);
                    });
                });
            });
            path("items", () -> {
                get(GroceryController::allItems);
                post(GroceryController::addItem);
                put(GroceryController::updateItem);
                path("{itemId}", () -> {
                    get(GroceryController::item);
                    delete(GroceryController::removeItem);
                });
            });
        };
    }

    private static void getItemsForCategoryAtStore(@NotNull Context context) {
        log.debug("request - getItemsForCategoryAtStore");
        String storeId = context.pathParam("storeId");
        log.debug("storeId - {}", storeId);
        String categoryId = context.pathParam("categoryId");
        log.debug("categoryId - {}", categoryId);
        context.json(groceryService.getItemsForCategoryAtStore(storeId, categoryId))
                .status(HttpStatus.OK);
    }

    private static void removeCategoryFromStoreLists(@NotNull Context context) {
        log.debug("request - removeCategoryFromStoreLists");
        String categoryId = context.pathParam("categoryId");
        log.debug("categoryId - {}", categoryId);
        context.json(groceryService.removeCategoryFromStoreLists(categoryId).stream()
                        .map(storeList -> storeList.getIn().getId().getString()).toList())
                .status(HttpStatus.OK);
    }

    private static void addCategoryToStoreLists(@NotNull Context context) {
        log.debug("request - addCategoryToStoreLists");
        String categoryId = context.pathParam("categoryId");
        log.debug("categoryId - {}", categoryId);
        context.json(groceryService.addCategoryToStoreLists(categoryId).stream()
                        .map(storeList -> storeList.getIn().getId().getString()).toList())
                .status(HttpStatus.CREATED);
    }

    private static void updateItem(@NotNull Context context) {
        log.debug("request - updateItem");
        ItemDetail itemDetail = context.bodyAsClass(ItemDetail.class);
        log.debug("itemDetail - {}", itemDetail);
        context.json(new RecordIdResponse("Updated Item",
                        groceryService.updateItem(itemDetail)))
                .status(HttpStatus.OK);
    }

    private static void removeItem(@NotNull Context context) {
        log.debug("request - removeItem");
        String itemId = context.pathParam("itemId");
        log.debug("itemId - {}", itemId);
        context.json(new RecordIdResponse("Removed Item", groceryService.removeItem(itemId).getId()))
                .status(HttpStatus.OK);
    }

    private static void item(@NotNull Context context) {
        log.debug("request - item");
        String itemId = context.pathParam("itemId");
        log.debug("itemId - {}", itemId);
        boolean detail = Boolean.parseBoolean(StringUtils.defaultIfBlank(context.queryParam("detail"), "false"));
        log.debug("detail - {}", detail);
        context.json(detail ? groceryService.itemDetail(itemId) : groceryService.item(itemId))
                .status(HttpStatus.OK);
    }

    private static void addItem(@NotNull Context context) {
        log.debug("request - addItem");
        boolean detail = Boolean.parseBoolean(StringUtils.defaultIfBlank(context.queryParam("detail"), "false"));
        log.debug("detail - {}", detail);
        RecordId responseId;
        if (detail) {
            ItemDetail itemDetail = context.bodyAsClass(ItemDetail.class);
            log.debug("itemDetail - {}", itemDetail);
            responseId = groceryService.addItemDetail(itemDetail);
        } else {
            Item item = context.bodyAsClass(Item.class);
            log.debug("item - {}", item);
            responseId = groceryService.addItem(item);
        }
        context.json(new RecordIdResponse("Added Item", responseId))
                .status(HttpStatus.CREATED);
    }

    private static void allItems(@NotNull Context context) {
        log.debug("request - allItems");
        context.json(groceryService.allItems())
                .status(HttpStatus.OK);
    }

    private static void removeCategory(@NotNull Context context) {
        log.debug("request - removeCategory");
        String categoryId = context.pathParam("categoryId");
        log.debug("categoryId - {}", categoryId);
        context.json(new RecordIdResponse("Removed Category", groceryService.removeCategory(categoryId).getId()))
                .status(HttpStatus.OK);
    }

    private static void category(@NotNull Context context) {
        log.debug("request - category");
        String categoryId = context.pathParam("categoryId");
        log.debug("categoryId - {}", categoryId);
        boolean detail = Boolean.parseBoolean(StringUtils.defaultIfBlank(context.queryParam("detail"), "false"));
        log.debug("detail - {}", detail);
        context.json(detail ? groceryService.categoryDetail(categoryId) : groceryService.category(categoryId))
                .status(HttpStatus.OK);
    }

    private static void addCategory(@NotNull Context context) {
        log.debug("request - addCategory");
        Category category = context.bodyAsClass(Category.class);
        log.debug("category - {}", category);
        context.json(new RecordIdResponse("Added Category", groceryService.addCategory(category)))
                .status(HttpStatus.CREATED);
    }

    private static void allCategories(@NotNull Context context) {
        log.debug("request - allCategories");
        boolean status = Boolean.parseBoolean(StringUtils.defaultIfBlank(context.queryParam("status"), "false"));
        log.debug("status - {}", status);
        context.json(status ? groceryService.allCategoriesWithStoreListStatus() : groceryService.allCategories())
                .status(HttpStatus.OK);
    }

    private static void removeStore(@NotNull Context context) {
        log.debug("request - removeStore");
        String storeId = context.pathParam("storeId");
        log.debug("storeId - {}", storeId);
        context.json(new RecordIdResponse("Removed Store", groceryService.removeStore(storeId).getId()))
                .status(HttpStatus.OK);
    }

    private static void store(@NotNull Context context) {
        log.debug("request - store");
        String storeId = context.pathParam("storeId");
        log.debug("storeId - {}", storeId);
        boolean detail = Boolean.parseBoolean(StringUtils.defaultIfBlank(context.queryParam("detail"), "false"));
        log.debug("detail - {}", detail);
        context.json(detail ? groceryService.storeDetail(storeId) : groceryService.store(storeId))
                .status(HttpStatus.OK);
    }

    private static void addStore(@NotNull Context context) {
        log.debug("request - addStore");
        Store store = context.bodyAsClass(Store.class);
        log.debug("store - {}", store);
        context.json(new RecordIdResponse("Added Store", groceryService.addStore(store)))
                .status(HttpStatus.CREATED);
    }

    private static void allStores(@NotNull Context context) {
        log.debug("request - allStores");
        context.json(groceryService.allStores())
                .status(HttpStatus.OK);
    }
}
