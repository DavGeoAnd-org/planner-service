package com.davgeoand.api.service;

import com.davgeoand.api.data.GroceryDB;
import com.davgeoand.api.exception.GroceryException;
import com.davgeoand.api.model.grocery.*;
import com.surrealdb.RecordId;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class GroceryService {
    private final GroceryDB groceryDB = new GroceryDB();

    public List<Item> allItems() {
        List<Item> itemList = new ArrayList<>();
        groceryDB.allItems().forEachRemaining(itemList::add);
        return itemList;
    }

    public List<Category> allCategories() {
        List<Category> categoryList = new ArrayList<>();
        groceryDB.allCategories().forEachRemaining(categoryList::add);
        return categoryList;
    }

    public List<Store> allStores() {
        List<Store> storeList = new ArrayList<>();
        groceryDB.allStores().forEachRemaining(storeList::add);
        return storeList;
    }

    public Item createItem(Item item) {
        log.debug("item - {}", item);
        Item createdItem = groceryDB.createItem(item);
        log.debug("createdItem - {}", createdItem);
        return createdItem;
    }

    public Category createCategory(Category category) {
        log.debug("category - {}", category);
        Category createdCategory = groceryDB.createCategory(category);
        log.debug("createdCategory - {}", createdCategory);
        return createdCategory;
    }

    public Store createStore(Store store) {
        log.debug("store - {}", store);
        Store createdStore = groceryDB.createStore(store);
        log.debug("createdStore - {}", createdStore);
        return createdStore;
    }

    public Item item(String id) {
        log.debug("id - {}", id);
        return groceryDB.item(id).orElseThrow(() -> new GroceryException.MissingItemException(id));
    }

    public Category category(String id) {
        log.debug("id - {}", id);
        return groceryDB.category(id).orElseThrow(() -> new GroceryException.MissingCategoryException(id));
    }

    public Store store(String id) {
        log.debug("id - {}", id);
        return groceryDB.store(id).orElseThrow(() -> new GroceryException.MissingStoreException(id));
    }

    public List<Category> allCategoriesForItem(String id) {
        log.debug("id - {}", id);
        Item item = item(id);
        List<Category> categoryList = new ArrayList<>();
        groceryDB.allCategoriesForItem(item.getId()).forEachRemaining(categoryList::add);
        return categoryList;
    }

    public ProductOf createProductOf(String itemId, String categoryId) {
        log.debug("itemId - {}", itemId);
        log.debug("categoryId - {}", categoryId);
        Item item = item(itemId);
        Category category = category(categoryId);
        ProductOf createdProductOf = groceryDB.createProductOf(item.getId(), category.getId());
        log.debug("createdProductOf - {}", createdProductOf);
        return createdProductOf;
    }

    public List<Item> allItemsForCategory(String id) {
        log.debug("id - {}", id);
        Category category = category(id);
        List<Item> itemList = new ArrayList<>();
        groceryDB.allItemsForCategory(category.getId()).forEachRemaining(itemList::add);
        return itemList;
    }

    public List<Store> allStoresForItem(String id) {
        log.debug("id - {}", id);
        Item item = item(id);
        List<Store> storeList = new ArrayList<>();
        groceryDB.allStoresForItem(item.getId()).forEachRemaining(storeList::add);
        return storeList;
    }

    public SoldAt createSoldAt(String itemId, String storeId, SoldAt soldAt) {
        log.debug("itemId - {}", itemId);
        log.debug("storeId - {}", storeId);
        log.debug("soldAt - {}", soldAt);
        Item item = item(itemId);
        Store store = store(storeId);
        SoldAt createdSoldAt = groceryDB.createSoldAt(item.getId(), store.getId(), soldAt);
        log.debug("createdSoldAt - {}", createdSoldAt);
        return createdSoldAt;
    }

    public List<Item> allItemsForStore(String id) {
        log.debug("id - {}", id);
        Store store = store(id);
        List<Item> itemList = new ArrayList<>();
        groceryDB.allItemsForStore(store.getId()).forEachRemaining(itemList::add);
        return itemList;
    }

    public List<StoreWithLocation> allStoresForItemWithLocation(String id) {
        log.debug("id - {}", id);
        Item item = item(id);
        List<StoreWithLocation> storeWithLocationList = new ArrayList<>();
        groceryDB.allStoresForItemWithLocation(item.getId()).forEachRemaining(storeWithLocationList::add);
        return storeWithLocationList;
    }

    public void updateList(String id, ListUpdate listUpdate) {
        log.debug("id - {}", id);
        log.debug("listUpdate - {}", listUpdate);
        Store store = store(id);
        List<RecordId> addRecordIdList = new ArrayList<>();
        listUpdate.getAdd().forEach(add -> addRecordIdList.add(item(add).getId()));
        List<RecordId> removeRecordIdList = new ArrayList<>();
        listUpdate.getRemove().forEach(remove -> removeRecordIdList.add(item(remove).getId()));
        groceryDB.updateList(store, addRecordIdList, removeRecordIdList);
    }

    public List<Item> storeList(String id) {
        log.debug("id - {}", id);
        Store store = store(id);
        List<Item> itemList = new ArrayList<>();
        groceryDB.storeList(store.getId()).forEachRemaining(itemList::add);
        return itemList;
    }
}
