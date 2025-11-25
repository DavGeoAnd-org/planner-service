package com.davgeoand.api.model.grocery.store;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ListUpdate {
    public List<String> add = new ArrayList<>(), remove = new ArrayList<>();
}