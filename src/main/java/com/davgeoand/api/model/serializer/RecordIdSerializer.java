package com.davgeoand.api.model.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.surrealdb.RecordId;

import java.io.IOException;

public class RecordIdSerializer extends StdSerializer<RecordId> {

    protected RecordIdSerializer() {
        super(RecordId.class);
    }

    @Override
    public void serialize(RecordId value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getTable() + ":" + value.getId().getString());
    }
}
