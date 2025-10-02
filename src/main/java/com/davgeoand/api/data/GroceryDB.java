package com.davgeoand.api.data;

import com.davgeoand.api.helper.Constants;
import com.davgeoand.api.model.grocery.*;
import com.surrealdb.Array;
import com.surrealdb.RecordId;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.signin.Root;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class GroceryDB {
    private static Surreal driver;
    private static final String SURREALDB_CONNECT = Constants.SURREALDB_CONNECT;
    private static final String SURREALDB_NAMESPACE = Constants.SURREALDB_NAMESPACE;
    private static final String SURREALDB_USERNAME = Constants.SURREALDB_USERNAME;
    private static final String SURREALDB_PASSWORD = Constants.SURREALDB_PASSWORD;

    public static void init() {
        log.info("Initializing grocery db");
        driver = new Surreal();
        driver.connect(SURREALDB_CONNECT)
                .useNs(SURREALDB_NAMESPACE)
                .useDb("grocery")
                .signin(new Root(SURREALDB_USERNAME, SURREALDB_PASSWORD));
        log.info("Initialized grocery db");
    }

    public static Iterator<Item> allItems() {
        return driver.select(Item.class, "items");
    }

    public static Iterator<Category> allCategories() {
        return driver.select(Category.class, "categories");
    }

    public static Iterator<Store> allStores() {
        return driver.select(Store.class, "stores");
    }

    public static Item createItem(Item item) {
        log.debug("item - {}", item);
        return driver.create(Item.class, new RecordId("items", item.getName()), item);
    }

    public static Category createCategory(Category category) {
        log.debug("category - {}", category);
        return driver.create(Category.class, new RecordId("categories", category.getName()), category);
    }

    public static Store createStore(Store store) {
        log.debug("store - {}", store);
        return driver.create(Store.class, new RecordId("stores", store.getName()), store);
    }

    public static Optional<Item> item(String id) {
        log.debug("id - {}", id);
        return driver.select(Item.class, new RecordId("items", id));
    }

    public static Optional<Category> category(String id) {
        log.debug("id - {}", id);
        return driver.select(Category.class, new RecordId("categories", id));
    }

    public static Optional<Store> store(String id) {
        log.debug("id - {}", id);
        return driver.select(Store.class, new RecordId("stores", id));
    }

    public static Iterator<Category> allCategoriesForItem(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("RETURN $id->product_of.out[*];",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(Category.class);
    }

    public static ProductOf createProductOf(RecordId itemId, RecordId categoryId) {
        log.debug("itemId - {}", itemId);
        log.debug("categoryId - {}", categoryId);
        return driver.relate(itemId, "product_of", categoryId).get(ProductOf.class);
    }

    public static Iterator<Item> allItemsForCategory(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("RETURN $id<-product_of.in[*];",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(Item.class);
    }

    public static Iterator<Store> allStoresForItem(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("RETURN $id->sold_at.out[*];",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(Store.class);
    }

    public static SoldAt createSoldAt(RecordId itemId, RecordId storeId, SoldAt soldAt) {
        log.debug("itemId - {}", itemId);
        log.debug("storeId - {}", storeId);
        log.debug("soldAt - {}", soldAt);
        return driver.relate(itemId, "sold_at", storeId, soldAt).get(SoldAt.class);
    }

    public static Iterator<Item> allItemsForStore(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("RETURN $id<-sold_at.in[*];",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(Item.class);
    }

    public static Iterator<StoreWithLocation> allStoresForItemWithLocation(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("""
                        RETURN $id->sold_at.map(|$val: any| { {
                        location: $val.location,
                        store: $val.out[*]
                        } });""",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(StoreWithLocation.class);
    }

    public static void updateList(Store store, List<RecordId> addRecordIdList, List<RecordId> removeRecordIdList) {
        log.debug("store - {}", store);
        log.debug("addRecordIdList - {}", addRecordIdList);
        log.debug("removeRecordIdList - {}", removeRecordIdList);
        addRecordIdList.forEach(addRecordId -> driver.relate(store.getId(), "list_item", addRecordId));
        removeRecordIdList.forEach(removeRecordId -> driver.queryBind("DELETE list_item WHERE in = $storeId AND out = $removeId;",
                Map.of("storeId", store.getId(), "removeId", removeRecordId)));
    }

    public static Iterator<Item> storeList(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("RETURN $id->list_item.out[*];",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(Item.class);
    }
}
