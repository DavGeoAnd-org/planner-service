package com.davgeoand.api.model.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.surrealdb.RecordId;

import java.io.IOException;

public class RecordIdDeserializer extends StdDeserializer<RecordId> {

    protected RecordIdDeserializer() {
        super(RecordId.class);
    }

    @Override
    public RecordId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        return new RecordId(node.get("table").asText(), node.get("id").asText());
    }

}
