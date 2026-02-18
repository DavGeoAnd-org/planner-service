package com.davgeoand.api.model.workout;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Step {
    Exercise exercise;
    String note;
    int order;
}
