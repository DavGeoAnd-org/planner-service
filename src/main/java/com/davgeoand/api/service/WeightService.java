package com.davgeoand.api.service;

import com.davgeoand.api.data.WeightDB;
import com.davgeoand.api.model.weight.WeightRecord;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class WeightService {
    private final WeightDB weightDB = new WeightDB();

    public List<WeightRecord> allWeightRecords() {
        List<WeightRecord> weightRecordList = new ArrayList<>();
        weightDB.allWeightRecords().forEachRemaining(weightRecordList::add);
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
