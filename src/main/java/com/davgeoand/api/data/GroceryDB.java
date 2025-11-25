package com.davgeoand.api.data;

import com.davgeoand.api.Constants;
import com.davgeoand.api.model.grocery.category.Category;
import com.davgeoand.api.model.grocery.item.Item;
import com.davgeoand.api.model.grocery.item.ProductOf;
import com.davgeoand.api.model.grocery.item.SoldAt;
import com.davgeoand.api.model.grocery.store.Store;
import com.davgeoand.api.model.grocery.store.StoreList;
import com.surrealdb.Array;
import com.surrealdb.RecordId;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.signin.Root;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class GroceryDB {
    private final Surreal driver;

    public GroceryDB() {
        log.info("Initializing grocery db");
        driver = new Surreal();
        driver.connect(Constants.SURREALDB_CONNECT)
                .useNs(Constants.SURREALDB_NAMESPACE)
                .useDb("grocery")
                .signin(new Root(Constants.SURREALDB_USERNAME, Constants.SURREALDB_PASSWORD));
        log.info("Initialized grocery db");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Store> allStores() {
        return driver.select(Store.class, "stores");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Store createStore(Store store) {
        log.debug("store - {}", store);
        return driver.create(Store.class, new RecordId("stores", store.getName()), store);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Category> allCategories() {
        return driver.select(Category.class, "categories");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Category createCategory(Category category) {
        log.debug("category - {}", category);
        return driver.create(Category.class, new RecordId("categories", category.getName()), category);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Item> allItems() {
        return driver.select(Item.class, "items");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Item createItem(Item item) {
        log.debug("item - {}", item);
        return driver.create(Item.class, new RecordId("items", item.getName()), item);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Optional<Category> category(String id) {
        log.debug("id - {}", id);
        return driver.select(Category.class, new RecordId("categories", id));
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public ProductOf createProductOf(RecordId itemId, RecordId categoryId) {
        log.debug("itemId - {}", itemId);
        log.debug("categoryId - {}", categoryId);
        return driver.relate(itemId, "product_of", categoryId).get(ProductOf.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public SoldAt createSoldAt(RecordId itemId, RecordId storeId, SoldAt soldAt) {
        log.debug("itemId - {}", itemId);
        log.debug("storeId - {}", storeId);
        log.debug("soldAt - {}", soldAt);
        return driver.relate(itemId, "sold_at", storeId, soldAt).get(SoldAt.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Optional<Store> store(String id) {
        log.debug("id - {}", id);
        return driver.select(Store.class, new RecordId("stores", id));
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<StoreList> addCategoryToLists(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind(
                "RELATE (SELECT id FROM stores WHERE array::group(<-sold_at.in->product_of.out) CONTAINS $id) -> store_list -> $id;",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(StoreList.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Category> storeList(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("RETURN array::group($id->store_list.out[*]);",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(Category.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<StoreList> removeCategoryToLists(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind(
                "DELETE store_list WHERE out = $id RETURN BEFORE;",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(StoreList.class);
    }
}
