package com.davgeoand.api.model.workout;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDetail extends Workout {
    List<Step> steps;
}
