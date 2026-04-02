package com.davgeoand.api.model.grocery.category;

import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CategoryWithStoreListStatus extends Category {
    boolean storeListStatus;
}
