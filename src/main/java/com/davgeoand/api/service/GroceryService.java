package com.davgeoand.api.service;

import com.davgeoand.api.data.GroceryDB;
import com.davgeoand.api.exception.GroceryException;
import com.davgeoand.api.model.grocery.*;
import com.surrealdb.RecordId;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GroceryService {
    public static List<Item> allItems() {
        List<Item> itemList = new ArrayList<>();
        GroceryDB.allItems().forEachRemaining(itemList::add);
        return itemList;
    }

    public static List<Category> allCategories() {
        List<Category> categoryList = new ArrayList<>();
        GroceryDB.allCategories().forEachRemaining(categoryList::add);
        return categoryList;
    }

    public static List<Store> allStores() {
        List<Store> storeList = new ArrayList<>();
        GroceryDB.allStores().forEachRemaining(storeList::add);
        return storeList;
    }

    public static Item createItem(Item item) {
        log.debug("item - {}", item);
        Item createdItem = GroceryDB.createItem(item);
        log.debug("createdItem - {}", createdItem);
        return createdItem;
    }

    public static Category createCategory(Category category) {
        log.debug("category - {}", category);
        Category createdCategory = GroceryDB.createCategory(category);
        log.debug("createdCategory - {}", createdCategory);
        return createdCategory;
    }

    public static Store createStore(Store store) {
        log.debug("store - {}", store);
        Store createdStore = GroceryDB.createStore(store);
        log.debug("createdStore - {}", createdStore);
        return createdStore;
    }

    public static Item item(String id) {
        log.debug("id - {}", id);
        return GroceryDB.item(id).orElseThrow(() -> new GroceryException.MissingItemException(id));
    }

    public static Category category(String id) {
        log.debug("id - {}", id);
        return GroceryDB.category(id).orElseThrow(() -> new GroceryException.MissingCategoryException(id));
    }

    public static Store store(String id) {
        log.debug("id - {}", id);
        return GroceryDB.store(id).orElseThrow(() -> new GroceryException.MissingStoreException(id));
    }

    public static List<Category> allCategoriesForItem(String id) {
        log.debug("id - {}", id);
        Item item = item(id);
        List<Category> categoryList = new ArrayList<>();
        GroceryDB.allCategoriesForItem(item.getId()).forEachRemaining(categoryList::add);
        return categoryList;
    }

    public static ProductOf createProductOf(String itemId, String categoryId) {
        log.debug("itemId - {}", itemId);
        log.debug("categoryId - {}", categoryId);
        Item item = item(itemId);
        Category category = category(categoryId);
        ProductOf createdProductOf = GroceryDB.createProductOf(item.getId(), category.getId());
        log.debug("createdProductOf - {}", createdProductOf);
        return createdProductOf;
    }

    public static List<Item> allItemsForCategory(String id) {
        log.debug("id - {}", id);
        Category category = category(id);
        List<Item> itemList = new ArrayList<>();
        GroceryDB.allItemsForCategory(category.getId()).forEachRemaining(itemList::add);
        return itemList;
    }

    public static List<Store> allStoresForItem(String id) {
        log.debug("id - {}", id);
        Item item = item(id);
        List<Store> storeList = new ArrayList<>();
        GroceryDB.allStoresForItem(item.getId()).forEachRemaining(storeList::add);
        return storeList;
    }

    public static SoldAt createSoldAt(String itemId, String storeId, SoldAt soldAt) {
        log.debug("itemId - {}", itemId);
        log.debug("storeId - {}", storeId);
        log.debug("soldAt - {}", soldAt);
        Item item = item(itemId);
        Store store = store(storeId);
        SoldAt createdSoldAt = GroceryDB.createSoldAt(item.getId(), store.getId(), soldAt);
        log.debug("createdSoldAt - {}", createdSoldAt);
        return createdSoldAt;
    }

    public static List<Item> allItemsForStore(String id) {
        log.debug("id - {}", id);
        Store store = store(id);
        List<Item> itemList = new ArrayList<>();
        GroceryDB.allItemsForStore(store.getId()).forEachRemaining(itemList::add);
        return itemList;
    }

    public static List<StoreWithLocation> allStoresForItemWithLocation(String id) {
        log.debug("id - {}", id);
        Item item = item(id);
        List<StoreWithLocation> storeWithLocationList = new ArrayList<>();
        GroceryDB.allStoresForItemWithLocation(item.getId()).forEachRemaining(storeWithLocationList::add);
        return storeWithLocationList;
    }

    public static void updateList(String id, ListUpdate listUpdate) {
        log.debug("id - {}", id);
        log.debug("listUpdate - {}", listUpdate);
        Store store = store(id);
        List<RecordId> addRecordIdList = new ArrayList<>();
        listUpdate.getAdd().forEach(add -> addRecordIdList.add(item(add).getId()));
        List<RecordId> removeRecordIdList = new ArrayList<>();
        listUpdate.getRemove().forEach(remove -> removeRecordIdList.add(item(remove).getId()));
        GroceryDB.updateList(store, addRecordIdList, removeRecordIdList);
    }

    public static List<Item> storeList(String id) {
        log.debug("id - {}", id);
        Store store = store(id);
        List<Item> itemList = new ArrayList<>();
        GroceryDB.storeList(store.getId()).forEachRemaining(itemList::add);
        return itemList;
    }
}
