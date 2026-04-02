package com.davgeoand.api.model.grocery.item;

import com.davgeoand.api.model.grocery.category.Category;
import com.davgeoand.api.model.grocery.store.StoreWithLocation;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemDetail extends Item {
    Category category;
    List<StoreWithLocation> stores;
}
