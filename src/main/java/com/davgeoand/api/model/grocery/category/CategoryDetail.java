package com.davgeoand.api.model.grocery.category;

import com.davgeoand.api.model.grocery.item.Item;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetail extends Category {
    List<Item> items;
}
