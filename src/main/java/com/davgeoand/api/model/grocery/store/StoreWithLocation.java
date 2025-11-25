package com.davgeoand.api.model.grocery.store;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StoreWithLocation {
    public Store store;
    public String location;
}