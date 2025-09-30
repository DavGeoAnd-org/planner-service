package com.davgeoand.api.service;

import com.davgeoand.api.data.GroceryDB;
import com.davgeoand.api.exception.GroceryException;
import com.davgeoand.api.model.grocery.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GroceryService {
    public static List<Item> allItems() {
        List<Item> itemList = new ArrayList<>();
        GroceryDB.selectAllItems().forEachRemaining(itemList::add);
        return itemList;
    }

    public static Item itemById(String id) {
        log.debug("id - {}", id);
        return GroceryDB.selectItemById(id).orElseThrow(() -> new GroceryException.MissingItemException(id));
    }

    public static List<Category> allCategories() {
        List<Category> categoryList = new ArrayList<>();
        GroceryDB.selectAllCategories().forEachRemaining(categoryList::add);
        log.debug("categoryList - {}", categoryList);

        return categoryList;
    }

    public static List<Store> allStores() {
        List<Store> storeList = new ArrayList<>();
        GroceryDB.selectAllStores().forEachRemaining(storeList::add);
        return storeList;
    }

    public static List<Category> allCategoriesForItemByItemId(String id) {
        log.debug("id - {}", id);
        Item item = GroceryDB.selectItemById(id).orElseThrow(() -> new GroceryException.MissingItemException(id));
        List<Category> categoryList = new ArrayList<>();
        GroceryDB.selectAllCategoriesForItemByItemRecordId(item.getId()).forEachRemaining(categoryList::add);
        return categoryList;
    }

    public static Category categoryById(String id) {
        log.debug("id - {}", id);
        return GroceryDB.selectCategoryById(id).orElseThrow(() -> new GroceryException.MissingCategoryException(id));
    }

    public static List<Item> allItemsForCategoryByCategoryId(String id) {
        log.debug("id - {}", id);
        Category category = GroceryDB.selectCategoryById(id)
                .orElseThrow(() -> new GroceryException.MissingCategoryException(id));
        List<Item> itemList = new ArrayList<>();
        GroceryDB.selectAllItemsForCategoryByCategoryRecordId(category.getId()).forEachRemaining(itemList::add);
        return itemList;
    }

    public static Store storeById(String id) {
        log.debug("id - {}", id);
        return GroceryDB.selectStoreById(id).orElseThrow(() -> new GroceryException.MissingStoreException(id));
    }

    public static List<Store> allStoresForItemByItemId(String id) {
        log.debug("id - {}", id);
        Item item = GroceryDB.selectItemById(id).orElseThrow(() -> new GroceryException.MissingItemException(id));
        List<Store> storeList = new ArrayList<>();
        GroceryDB.selectAllStoresForItemByItemRecordId(item.getId()).forEachRemaining(storeList::add);
        return storeList;
    }

    public static List<Item> allItemsForStoreByStoreId(String id) {
        log.debug("id - {}", id);
        Store store = GroceryDB.selectStoreById(id)
                .orElseThrow(() -> new GroceryException.MissingStoreException(id));
        List<Item> itemList = new ArrayList<>();
        GroceryDB.selectAllItemsForStoreByStoreRecordId(store.getId()).forEachRemaining(itemList::add);
        return itemList;
    }

    public static Item addItem(Item item) {
        log.debug("item - {}", item);
        Item createdItem = GroceryDB.createAddItem(item);
        log.debug("createdItem - {}", createdItem);
        return createdItem;
    }

    public static Item updateItem(String id, Item item) {
        log.debug("id - {}", id);
        log.debug("item - {}", item);
        Item existingItem = itemById(id);
        log.debug("existingItem - {}", existingItem);
        Item updatedItem = GroceryDB.updateUpdateItem(existingItem.getId(), item);
        log.debug("updatedItem - {}", updatedItem);
        return updatedItem;
    }

    public static Category addCategory(Category category) {
        log.debug("category - {}", category);
        Category createdCategory = GroceryDB.createAddCategory(category);
        log.debug("createdCategory - {}", createdCategory);
        return createdCategory;
    }

    public static Store addStore(Store store) {
        log.debug("store - {}", store);
        Store createdStore = GroceryDB.createAddStore(store);
        log.debug("createdStore - {}", createdStore);
        return createdStore;
    }

    public static ProductOf addProductOf(String itemId, String categoryId) {
        log.debug("itemId - {}", itemId);
        log.debug("categoryId - {}", categoryId);
        Item item = itemById(itemId);
        Category category = categoryById(categoryId);
        ProductOf createdProductOf = GroceryDB.relateAddProductOf(item.getId(), category.getId());
        log.debug("createdProductOf - {}", createdProductOf);
        return createdProductOf;
    }

    public static SoldAt addSoldAt(String itemId, String storeId, SoldAt soldAt) {
        log.debug("itemId - {}", itemId);
        log.debug("storeId - {}", storeId);
        log.debug("soldAt - {}", soldAt);
        Item item = itemById(itemId);
        Store store = storeById(storeId);
        SoldAt createdSoldAt = GroceryDB.relateAddSoldAt(item.getId(), store.getId(), soldAt);
        log.debug("createdSoldAt - {}", createdSoldAt);
        return createdSoldAt;
    }
}
