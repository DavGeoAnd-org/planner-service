package com.davgeoand.api.service;

import com.davgeoand.api.data.GroceryDB;
import com.davgeoand.api.exception.GroceryException;
import com.davgeoand.api.model.grocery.category.Category;
import com.davgeoand.api.model.grocery.item.Item;
import com.davgeoand.api.model.grocery.item.ItemFullDetail;
import com.davgeoand.api.model.grocery.item.ProductOf;
import com.davgeoand.api.model.grocery.item.SoldAt;
import com.davgeoand.api.model.grocery.store.Store;
import com.davgeoand.api.model.grocery.store.StoreList;
import com.davgeoand.api.model.grocery.store.StoreWithLocation;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class GroceryService {
    private final GroceryDB groceryDB = new GroceryDB();

    public List<Store> allStores() {
        List<Store> storeList = new ArrayList<>();
        groceryDB.allStores().forEachRemaining(storeList::add);
        log.debug("storeList - {}", storeList);
        return storeList;
    }

    public Store createStore(Store store) {
        log.debug("store - {}", store);
        Store createdStore = groceryDB.createStore(store);
        log.debug("createdStore - {}", createdStore);
        return createdStore;
    }

    public List<Category> allCategories() {
        List<Category> categoryList = new ArrayList<>();
        groceryDB.allCategories().forEachRemaining(categoryList::add);
        log.debug("categoryList - {}", categoryList);
        return categoryList;
    }

    public Category createCategory(Category category) {
        log.debug("category - {}", category);
        Category createdCategory = groceryDB.createCategory(category);
        log.debug("createdCategory - {}", createdCategory);
        return createdCategory;
    }

    public List<Item> allItems() {
        List<Item> itemList = new ArrayList<>();
        groceryDB.allItems().forEachRemaining(itemList::add);
        log.debug("itemList - {}", itemList);
        return itemList;
    }

    public Item createItem(ItemFullDetail itemFullDetail) {
        log.debug("itemFullDetail - {}", itemFullDetail);
        Item createdItem = groceryDB.createItem(itemFullDetail.getItem());
        log.debug("createdItem - {}", createdItem);
        Category category = category(itemFullDetail.getCategory().getName());
        log.debug("category - {}", category);
        ProductOf createProductOf = groceryDB.createProductOf(createdItem.getId(), category.getId());
        log.debug("createProductOf - {}", createProductOf);
        for (StoreWithLocation storeWithLocation : itemFullDetail.getStores()) {
            SoldAt createSoldAt = groceryDB.createSoldAt(createdItem.getId(),
                    store(storeWithLocation.getStore().getName()).getId(), new SoldAt(storeWithLocation.getLocation()));
            log.debug("createSoldAt - {}", createSoldAt);
        }
        return createdItem;
    }

    public Category category(String id) {
        log.debug("id - {}", id);
        return groceryDB.category(id).orElseThrow(() -> new GroceryException.MissingCategoryException(id));
    }

    public Store store(String id) {
        log.debug("id - {}", id);
        return groceryDB.store(id).orElseThrow(() -> new GroceryException.MissingStoreException(id));
    }

    public List<StoreList> addCategoryToLists(String categoryId) {
        log.debug("categoryId - {}", categoryId);
        Category category = category(categoryId);
        log.debug("category - {}", category);
        List<StoreList> storeListList = new ArrayList<>();
        groceryDB.addCategoryToLists(category.getId()).forEachRemaining(storeListList::add);
        log.debug("storeListList - {}", storeListList);
        return storeListList;
    }

    public List<Category> storeList(String id) {
        log.debug("id - {}", id);
        List<Category> categoryList = new ArrayList<>();
        groceryDB.storeList(store(id).getId()).forEachRemaining(categoryList::add);
        log.debug("categoryList - {}", categoryList);
        return categoryList;
    }

    public List<StoreList> removeCategoryToLists(String categoryId) {
        log.debug("categoryId - {}", categoryId);
        Category category = category(categoryId);
        log.debug("category - {}", category);
        List<StoreList> storeListList = new ArrayList<>();
        groceryDB.removeCategoryToLists(category.getId()).forEachRemaining(storeListList::add);
        log.debug("storeListList - {}", storeListList);
        return storeListList;
    }
}
