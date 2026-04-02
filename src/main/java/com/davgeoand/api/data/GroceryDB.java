package com.davgeoand.api.data;

import com.davgeoand.api.ServiceProperties;
import com.davgeoand.api.exception.JavalinServiceException.MissingPropertyException;
import com.davgeoand.api.model.grocery.category.Category;
import com.davgeoand.api.model.grocery.category.CategoryDetail;
import com.davgeoand.api.model.grocery.category.CategoryWithStoreListStatus;
import com.davgeoand.api.model.grocery.item.*;
import com.davgeoand.api.model.grocery.store.Store;
import com.davgeoand.api.model.grocery.store.StoreDetail;
import com.davgeoand.api.model.grocery.store.StoreList;
import com.surrealdb.RecordId;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.signin.RootCredential;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class GroceryDB {
    private final Surreal driver;
    private final String SURREALDB_CONNECT = ServiceProperties.getProperty("surrealdb.connect").orElseThrow(() -> new MissingPropertyException("surrealdb.connect"));
    private final String SURREALDB_NAMESPACE = ServiceProperties.getProperty("surrealdb.namespace").orElseThrow(() -> new MissingPropertyException("surrealdb.namespace"));
    private final String SURREALDB_USERNAME = ServiceProperties.getProperty("surrealdb.username").orElseThrow(() -> new MissingPropertyException("surrealdb.username"));
    private final String SURREALDB_PASSWORD = ServiceProperties.getProperty("surrealdb.password").orElseThrow(() -> new MissingPropertyException("surrealdb.password"));

    public GroceryDB() {
        log.info("Initializing grocery db");
        driver = new Surreal();
        connect();
        log.info("Initialized grocery db");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    private void connect() {
        log.info("Connecting to grocery db");
        driver.connect(SURREALDB_CONNECT)
                .useNs(SURREALDB_NAMESPACE)
                .useDb("grocery")
                .signin(new RootCredential(SURREALDB_USERNAME, SURREALDB_PASSWORD));
        log.info("Connected to grocery db");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Store> allStores() {
        return driver.select(Store.class, "stores");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Store addStore(Store store) {
        log.debug("store - {}", store);
        return driver.create(Store.class, new RecordId("stores", store.getName().toUpperCase()), store);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Optional<Store> store(String storeId) {
        log.debug("storeId - {}", storeId);
        return driver.select(Store.class, new RecordId("stores", storeId));
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public void removeStore(RecordId storeId) {
        log.debug("storeId - {}", storeId);
        driver.delete(storeId);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Category> allCategories() {
        return driver.select(Category.class, "categories");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Category addCategory(Category category) {
        log.debug("category - {}", category);
        return driver.create(Category.class, new RecordId("categories", category.getName().toUpperCase()), category);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Optional<Category> category(String categoryId) {
        log.debug("categoryId - {}", categoryId);
        return driver.select(Category.class, new RecordId("categories", categoryId));
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public void removeCategory(RecordId categoryId) {
        log.debug("categoryId - {}", categoryId);
        driver.delete(categoryId);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<Item> allItems() {
        return driver.select(Item.class, "items");
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Item addItem(Item item) {
        log.debug("item - {}", item);
        return driver.create(Item.class, new RecordId("items", item.getName().toUpperCase()), item);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Optional<Item> item(String itemId) {
        log.debug("itemId - {}", itemId);
        return driver.select(Item.class, new RecordId("items", itemId));
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public void removeItem(RecordId itemId) {
        log.debug("itemId - {}", itemId);
        driver.delete(itemId);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public ItemDetail itemDetail(RecordId itemId) {
        log.debug("itemId - {}", itemId);
        Response response = driver.queryBind("""
                        SELECT *,
                        array::first(->product_of.out.*) AS category,
                        (SELECT location, out.id AS id, out.name AS name FROM $parent->sold_at) AS stores
                        FROM ONLY $itemId;""",
                Map.of("itemId", itemId));
        return response.take(0).get(ItemDetail.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public ProductOf addProductOf(ProductOf productOf) {
        log.debug("productOf - {}", productOf);
        return driver.relate(productOf.getIn(), "product_of", productOf.getOut()).get(ProductOf.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public SoldAt addSoldAt(SoldAt soldAt) {
        log.debug("soldAt - {}", soldAt);
        return driver.relate(soldAt.getIn(), "sold_at", soldAt.getOut(), soldAt).get(SoldAt.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public void removeProductOfFromItem(RecordId itemId) {
        log.debug("itemId - {}", itemId);
        driver.queryBind("DELETE product_of WHERE in == ($itemId);", Map.of("itemId", itemId));
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public void removeSoldAtFromItem(RecordId itemId) {
        log.debug("itemId - {}", itemId);
        driver.queryBind("DELETE sold_at WHERE in == ($itemId);", Map.of("itemId", itemId));
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<CategoryWithStoreListStatus> allCategoriesWithStoreListStatus() {
        Response response = driver.query("""
                SELECT *,
                RETURN (IF (SELECT count(id) FROM store_list WHERE out.id == $parent.id) { true } ELSE { false }) AS storeListStatus
                FROM categories;""");
        return response.take(0).getArray().iterator(CategoryWithStoreListStatus.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<StoreList> addCategoryToStoreLists(RecordId categoryId) {
        log.debug("categoryId - {}", categoryId);
        Response response = driver.queryBind(
                "RELATE (SELECT id FROM stores WHERE array::group(<-sold_at.in->product_of.out) CONTAINS $categoryId) -> store_list -> $categoryId;",
                Map.of("categoryId", categoryId));
        return response.take(0).getArray().iterator(StoreList.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<StoreList> removeCategoryFromStoreLists(RecordId categoryId) {
        log.debug("categoryId - {}", categoryId);
        Response response = driver.queryBind(
                "DELETE store_list WHERE out = $categoryId RETURN BEFORE;",
                Map.of("categoryId", categoryId));
        return response.take(0).getArray().iterator(StoreList.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public CategoryDetail categoryDetail(RecordId categoryId) {
        log.debug("categoryId - {}", categoryId);
        Response response = driver.queryBind(
                """
                        SELECT *,
                        <-product_of.in.* AS items
                        FROM ONLY $categoryId;""",
                Map.of("categoryId", categoryId));
        return response.take(0).get(CategoryDetail.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public StoreDetail storeDetail(RecordId storeId) {
        log.debug("storeId - {}", storeId);
        Response response = driver.queryBind(
                """
                        SELECT *,
                        ->store_list.out.* AS categories
                        FROM ONLY $storeId;""",
                Map.of("storeId", storeId));
        return response.take(0).get(StoreDetail.class);
    }

    @WithSpan(kind = SpanKind.CLIENT)
    public Iterator<ItemWithLocation> getItemsForCategoryAtStore(RecordId storeId, RecordId categoryId) {
        log.debug("storeId - {}", storeId);
        log.debug("categoryId - {}", categoryId);
        Response response = driver.queryBind(
                """
                        SELECT *,
                        array::first(->(sold_at WHERE out == $storeId).location) AS location
                        FROM items
                        WHERE ->(sold_at WHERE out == $storeId)
                        AND ->(product_of WHERE out == $categoryId);""",
                Map.of("storeId", storeId, "categoryId", categoryId));
        return response.take(0).getArray().iterator(ItemWithLocation.class);
    }
}
