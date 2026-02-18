package com.davgeoand.api.model.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.surrealdb.RecordId;

import java.io.IOException;

public class RecordIdDeserializer extends StdDeserializer<RecordId> {

    protected RecordIdDeserializer() {
        super(RecordId.class);
    }

    @Override
    public RecordId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String id = p.getText();
        int separatorIndex = id.indexOf(":");
        return new RecordId(id.substring(0, separatorIndex), id.substring(separatorIndex + 1));
    }
}
