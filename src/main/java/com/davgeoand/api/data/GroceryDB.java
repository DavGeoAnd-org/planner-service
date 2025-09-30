package com.davgeoand.api.data;

import com.davgeoand.api.exception.JavalinServiceException;
import com.davgeoand.api.helper.Constants;
import com.davgeoand.api.helper.ServiceProperties;
import com.davgeoand.api.model.grocery.*;
import com.surrealdb.*;
import com.surrealdb.signin.Root;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class GroceryDB {
    private static Surreal driver;
    private static final String SURREALDB_CONNECT = ServiceProperties.getProperty(Constants.SURREALDB_CONNECT)
            .orElseThrow(() -> new JavalinServiceException.MissingPropertyException(Constants.SURREALDB_CONNECT));
    private static final String SURREALDB_NAMESPACE = ServiceProperties.getProperty(Constants.SURREALDB_NAMESPACE)
            .orElseThrow(() -> new JavalinServiceException.MissingPropertyException(Constants.SURREALDB_NAMESPACE));
    private static final String SURREALDB_USERNAME = ServiceProperties.getProperty(Constants.SURREALDB_USERNAME)
            .orElseThrow(() -> new JavalinServiceException.MissingPropertyException(Constants.SURREALDB_USERNAME));
    private static final String SURREALDB_PASSWORD = ServiceProperties.getProperty(Constants.SURREALDB_PASSWORD)
            .orElseThrow(() -> new JavalinServiceException.MissingPropertyException(Constants.SURREALDB_PASSWORD));

    public static void init() {
        log.info("Initializing grocery db");
        driver = new Surreal();
        driver.connect(SURREALDB_CONNECT)
                .useNs(SURREALDB_NAMESPACE)
                .useDb("grocery")
                .signin(new Root(SURREALDB_USERNAME, SURREALDB_PASSWORD));
        log.info("Initialized grocery db");
    }

    public static Iterator<Item> selectAllItems() {
        return driver.select(Item.class, "items");
    }

    public static Optional<Item> selectItemById(String id) {
        log.debug("id - {}", id);
        return driver.select(Item.class, new RecordId("items", id));
    }

    public static Iterator<Category> selectAllCategories() {
        return driver.select(Category.class, "categories");
    }

    public static Iterator<Store> selectAllStores() {
        return driver.select(Store.class, "stores");
    }

    public static Iterator<Category> selectAllCategoriesForItemByItemRecordId(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("RETURN $id->product_of.out[*];",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(Category.class);
    }

    public static Optional<Category> selectCategoryById(String id) {
        log.debug("id - {}", id);
        return driver.select(Category.class, new RecordId("categories", id));
    }

    public static Optional<Store> selectStoreById(String id) {
        log.debug("id - {}", id);
        return driver.select(Store.class, new RecordId("stores", id));
    }

    public static Iterator<Item> selectAllItemsForCategoryByCategoryRecordId(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("RETURN $id<-product_of.in[*];",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(Item.class);
    }

    public static Iterator<Store> selectAllStoresForItemByItemRecordId(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("RETURN $id->sold_at.out[*];",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(Store.class);
    }

    public static Iterator<Item> selectAllItemsForStoreByStoreRecordId(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("RETURN $id<-sold_at.in[*];",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(Item.class);
    }

    public static Item createAddItem(Item item) {
        log.debug("item - {}", item);
        return driver.create(Item.class, new RecordId("items", item.getName()), item);
    }

    public static Item updateUpdateItem(RecordId id, Item item) {
        log.debug("id - {}", id);
        log.debug("item - {}", item);
        return driver.update(Item.class, id, UpType.MERGE, item);
    }

    public static Category createAddCategory(Category category) {
        log.debug("category - {}", category);
        return driver.create(Category.class, new RecordId("categories", category.getName()), category);
    }

    public static Store createAddStore(Store store) {
        log.debug("store - {}", store);
        return driver.create(Store.class, new RecordId("stores", store.getName()), store);
    }

    public static ProductOf relateAddProductOf(RecordId itemId, RecordId categoryId) {
        log.debug("itemId - {}", itemId);
        log.debug("categoryId - {}", categoryId);
        return driver.relate(itemId, "product_of", categoryId).get(ProductOf.class);
    }

    public static SoldAt relateAddSoldAt(RecordId itemId, RecordId storeId, SoldAt soldAt) {
        log.debug("itemId - {}", itemId);
        log.debug("storeId - {}", storeId);
        log.debug("soldAt - {}", soldAt);
        return driver.relate(itemId, "sold_at", storeId, soldAt).get(SoldAt.class);
    }
}
