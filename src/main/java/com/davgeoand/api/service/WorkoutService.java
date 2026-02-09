package com.davgeoand.api.service;

import com.davgeoand.api.data.WorkoutDB;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class WorkoutService {
    private final WorkoutDB workoutDB = new WorkoutDB();
}
