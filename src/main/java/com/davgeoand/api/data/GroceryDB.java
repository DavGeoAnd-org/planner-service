package com.davgeoand.api.data;

import com.davgeoand.api.helper.Constants;
import com.davgeoand.api.model.grocery.*;
import com.surrealdb.Array;
import com.surrealdb.RecordId;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.signin.Root;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class GroceryDB {
    private final Surreal driver;

    @WithSpan(kind = SpanKind.CLIENT)
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
    public Iterator<Item> allItems() {
        return driver.select(Item.class, "items");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Category> allCategories() {
        return driver.select(Category.class, "categories");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Store> allStores() {
        return driver.select(Store.class, "stores");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Item createItem(Item item) {
        log.debug("item - {}", item);
        return driver.create(Item.class, new RecordId("items", item.getName()), item);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Category createCategory(Category category) {
        log.debug("category - {}", category);
        return driver.create(Category.class, new RecordId("categories", category.getName()), category);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Store createStore(Store store) {
        log.debug("store - {}", store);
        return driver.create(Store.class, new RecordId("stores", store.getName()), store);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Optional<Item> item(String id) {
        log.debug("id - {}", id);
        return driver.select(Item.class, new RecordId("items", id));
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Optional<Category> category(String id) {
        log.debug("id - {}", id);
        return driver.select(Category.class, new RecordId("categories", id));
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Optional<Store> store(String id) {
        log.debug("id - {}", id);
        return driver.select(Store.class, new RecordId("stores", id));
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Category> allCategoriesForItem(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("RETURN $id->product_of.out[*];",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(Category.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public ProductOf createProductOf(RecordId itemId, RecordId categoryId) {
        log.debug("itemId - {}", itemId);
        log.debug("categoryId - {}", categoryId);
        return driver.relate(itemId, "product_of", categoryId).get(ProductOf.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Item> allItemsForCategory(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("RETURN $id<-product_of.in[*];",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(Item.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Store> allStoresForItem(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("RETURN $id->sold_at.out[*];",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(Store.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public SoldAt createSoldAt(RecordId itemId, RecordId storeId, SoldAt soldAt) {
        log.debug("itemId - {}", itemId);
        log.debug("storeId - {}", storeId);
        log.debug("soldAt - {}", soldAt);
        return driver.relate(itemId, "sold_at", storeId, soldAt).get(SoldAt.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Item> allItemsForStore(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("RETURN $id<-sold_at.in[*];",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(Item.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<StoreWithLocation> allStoresForItemWithLocation(RecordId id) {
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

    @WithSpan(kind = SpanKind.CLIENT)
    public void updateList(Store store, List<RecordId> addRecordIdList, List<RecordId> removeRecordIdList) {
        log.debug("store - {}", store);
        log.debug("addRecordIdList - {}", addRecordIdList);
        log.debug("removeRecordIdList - {}", removeRecordIdList);
        addRecordIdList.forEach(addRecordId -> driver.relate(store.getId(), "list_item", addRecordId));
        removeRecordIdList.forEach(removeRecordId -> driver.queryBind("DELETE list_item WHERE in = $storeId AND out = $removeId;",
                Map.of("storeId", store.getId(), "removeId", removeRecordId)));
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Item> storeList(RecordId id) {
        log.debug("id - {}", id);
        Response response = driver.queryBind("RETURN $id->list_item.out[*];",
                Map.of("id", id));
        Array results = response.take(0).getArray();
        log.debug("results - {}", results);
        return results.iterator(Item.class);
    }
}
