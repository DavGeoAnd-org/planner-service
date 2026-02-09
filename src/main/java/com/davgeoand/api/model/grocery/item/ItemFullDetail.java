package com.davgeoand.api.model.grocery.item;

import com.davgeoand.api.model.grocery.category.Category;
import com.davgeoand.api.model.grocery.store.StoreWithLocation;
import lombok.*;

import java.util.List;

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
