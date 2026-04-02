package com.davgeoand.api.service;

import com.davgeoand.api.data.GroceryDB;
import com.davgeoand.api.exception.GroceryException;
import com.davgeoand.api.model.grocery.category.Category;
import com.davgeoand.api.model.grocery.category.CategoryDetail;
import com.davgeoand.api.model.grocery.category.CategoryWithStoreListStatus;
import com.davgeoand.api.model.grocery.item.*;
import com.davgeoand.api.model.grocery.store.Store;
import com.davgeoand.api.model.grocery.store.StoreDetail;
import com.davgeoand.api.model.grocery.store.StoreList;
import com.davgeoand.api.model.grocery.store.StoreWithLocation;
import com.surrealdb.RecordId;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@Slf4j
@NoArgsConstructor
public class GroceryService {
    private final GroceryDB groceryDB = new GroceryDB();

    public List<Store> allStores() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(groceryDB.allStores(), Spliterator.ORDERED), false).toList();
    }

    public RecordId addStore(Store store) {
        log.debug("store - {}", store);
        return groceryDB.addStore(store).getId();
    }

    public Store store(String storeId) {
        log.debug("storeId - {}", storeId);
        return groceryDB.store(storeId).orElseThrow(() -> new GroceryException.MissingStoreException(storeId));
    }

    public Store removeStore(String storeId) {
        log.debug("storeId - {}", storeId);
        Store store = store(storeId);
        log.debug("store - {}", store);
        groceryDB.removeStore(store.getId());
        return store;
    }

    public List<Category> allCategories() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(groceryDB.allCategories(), Spliterator.ORDERED), false).toList();
    }

    public RecordId addCategory(Category category) {
        log.debug("category - {}", category);
        return groceryDB.addCategory(category).getId();
    }

    public Category category(String categoryId) {
        log.debug("categoryId - {}", categoryId);
        return groceryDB.category(categoryId).orElseThrow(() -> new GroceryException.MissingCategoryException(categoryId));
    }

    public Category removeCategory(String categoryId) {
        log.debug("categoryId - {}", categoryId);
        Category category = category(categoryId);
        log.debug("category - {}", category);
        groceryDB.removeCategory(category.getId());
        return category;
    }

    public List<Item> allItems() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(groceryDB.allItems(), Spliterator.ORDERED), false).toList();
    }

    public RecordId addItem(Item item) {
        log.debug("item - {}", item);
        return groceryDB.addItem(item).getId();
    }

    public Item item(String itemId) {
        log.debug("itemId - {}", itemId);
        return groceryDB.item(itemId).orElseThrow(() -> new GroceryException.MissingItemException(itemId));
    }

    public Item removeItem(String itemId) {
        log.debug("itemId - {}", itemId);
        Item item = item(itemId);
        log.debug("item - {}", item);
        groceryDB.removeItem(item.getId());
        return item;
    }

    public ItemDetail itemDetail(String itemId) {
        log.debug("itemId - {}", itemId);
        return groceryDB.itemDetail(item(itemId).getId());
    }

    public RecordId addItemDetail(ItemDetail itemDetail) {
        log.debug("itemDetail - {}", itemDetail);
        RecordId itemId = addItem(new Item(null, itemDetail.getName()));
        log.debug("itemId - {}", itemId);
        ProductOf productOf = groceryDB.addProductOf(new ProductOf(null, itemId, itemDetail.getCategory().getId()));
        log.debug("productOf - {}", productOf);
        for (StoreWithLocation storeWithLocation : itemDetail.getStores()) {
            SoldAt soldAt = groceryDB.addSoldAt(new SoldAt(null, itemId, storeWithLocation.getId(), storeWithLocation.getLocation()));
            log.debug("soldAt - {}", soldAt);
        }
        return itemId;
    }

    public RecordId updateItem(ItemDetail itemDetail) {
        log.debug("itemDetail - {}", itemDetail);
        groceryDB.removeProductOfFromItem(itemDetail.getId());
        groceryDB.removeSoldAtFromItem(itemDetail.getId());
        ProductOf productOf = groceryDB.addProductOf(new ProductOf(null, itemDetail.getId(), itemDetail.getCategory().getId()));
        log.debug("productOf - {}", productOf);
        for (StoreWithLocation storeWithLocation : itemDetail.getStores()) {
            SoldAt soldAt = groceryDB.addSoldAt(new SoldAt(null, itemDetail.getId(), storeWithLocation.getId(), storeWithLocation.getLocation()));
            log.debug("soldAt - {}", soldAt);
        }
        return itemDetail.getId();
    }

    public List<CategoryWithStoreListStatus> allCategoriesWithStoreListStatus() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(groceryDB.allCategoriesWithStoreListStatus(), Spliterator.ORDERED), false).toList();
    }

    public List<StoreList> addCategoryToStoreLists(String categoryId) {
        log.debug("categoryId - {}", categoryId);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(groceryDB.addCategoryToStoreLists(category(categoryId).getId()), Spliterator.ORDERED), false).toList();

    }

    public List<StoreList> removeCategoryFromStoreLists(String categoryId) {
        log.debug("categoryId - {}", categoryId);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(groceryDB.removeCategoryFromStoreLists(category(categoryId).getId()), Spliterator.ORDERED), false).toList();
    }

    public CategoryDetail categoryDetail(String categoryId) {
        log.debug("categoryId - {}", categoryId);
        return groceryDB.categoryDetail(category(categoryId).getId());
    }

    public StoreDetail storeDetail(String storeId) {
        log.debug("storeId - {}", storeId);
        return groceryDB.storeDetail(store(storeId).getId());
    }

    public List<ItemWithLocation> getItemsForCategoryAtStore(String storeId, String categoryId) {
        log.debug("storeId - {}", storeId);
        log.debug("categoryId - {}", categoryId);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(groceryDB.getItemsForCategoryAtStore(store(storeId).getId(), category(categoryId).getId()), Spliterator.ORDERED), false).toList();
    }
}
