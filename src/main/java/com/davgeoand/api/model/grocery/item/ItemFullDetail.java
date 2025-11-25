package com.davgeoand.api.model.grocery.item;

import java.util.List;

import com.davgeoand.api.model.grocery.category.Category;
import com.davgeoand.api.model.grocery.store.StoreWithLocation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemFullDetail {
    Item item;
    Category category;
    List<StoreWithLocation> stores;
}
