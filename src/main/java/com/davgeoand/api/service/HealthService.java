package com.davgeoand.api.service;

import com.davgeoand.api.data.HealthDB;
import com.davgeoand.api.model.health.WeightRecord;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@Slf4j
@NoArgsConstructor
public class HealthService {
    private final HealthDB healthDB = new HealthDB();

    public List<WeightRecord> allWeightRecords() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(healthDB.allWeightRecords(), Spliterator.ORDERED), false).toList();
    }

    public WeightRecord addWeightRecord(WeightRecord weightRecord) {
        log.debug("weightRecord - {}", weightRecord);
        return healthDB.addWeightRecord(weightRecord);
    }

    public List<WeightRecord> weightRecordsByDayRange(int days) {
        log.debug("days - {}", days);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(healthDB.weightRecordsByDayRange(days), Spliterator.ORDERED), false).toList();
    }
}
