package com.davgeoand.api.service;

import com.davgeoand.api.data.HealthDB;
import com.davgeoand.api.model.health.WeightRecord;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class HealthService {
    private final HealthDB weightDB = new HealthDB();

    public List<WeightRecord> allWeightRecords() {
        List<WeightRecord> weightRecordList = new ArrayList<>();
        weightDB.allWeightRecords().forEachRemaining(weightRecordList::add);
        log.debug("weightRecordList - {}", weightRecordList);
        return weightRecordList;
    }

    public WeightRecord addWeightRecord(WeightRecord weightRecord) {
        log.debug("weightRecord - {}", weightRecord);
        weightRecord.setTimestamp(ZonedDateTime.now());
        WeightRecord createdWeightRecord = weightDB.addWeightRecord(weightRecord);
        log.debug("createdWeightRecord - {}", createdWeightRecord);
        return createdWeightRecord;
    }
}
