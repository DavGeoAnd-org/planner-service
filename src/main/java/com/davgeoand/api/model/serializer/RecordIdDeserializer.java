package com.davgeoand.api.model.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.surrealdb.RecordId;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;

public class RecordIdDeserializer extends StdDeserializer<RecordId> {

    protected RecordIdDeserializer() {
        super(RecordId.class);
    }

    @Override
    public RecordId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String recordId = p.getText();
        if (recordId.isEmpty()) {
            return null;
        } else {
            int separatorIndex = recordId.indexOf(":");
            String table = recordId.substring(0, separatorIndex);
            String id = recordId.substring(separatorIndex + 1);
            if (NumberUtils.isCreatable(id)) {
                return new RecordId(table, Long.parseLong(id));
            } else {
                return new RecordId(table, id);
            }
        }
    }
}