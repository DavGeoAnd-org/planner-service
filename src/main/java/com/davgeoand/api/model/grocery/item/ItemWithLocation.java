package com.davgeoand.api.model.grocery.item;

import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemWithLocation extends Item {
    String location;
}
