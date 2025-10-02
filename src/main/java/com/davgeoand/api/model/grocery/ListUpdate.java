package com.davgeoand.api.model.grocery;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ListUpdate {
    public List<String> add, remove;
}