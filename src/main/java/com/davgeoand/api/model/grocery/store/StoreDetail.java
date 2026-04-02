package com.davgeoand.api.model.grocery.store;

import com.davgeoand.api.model.grocery.category.Category;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class StoreDetail extends Store{
    List<Category> categories;
}
