package com.davgeoand.api.model.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.surrealdb.RecordId;

public class RecordIdSerializer extends StdSerializer<RecordId> {

    protected RecordIdSerializer() {
        super(RecordId.class);
    }

    @Override
    public void serialize(RecordId value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("id", value.getId().getString());
        gen.writeStringField("table", value.getTable());

        gen.writeEndObject();
    }

}
