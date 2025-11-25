package com.davgeoand.api.model.grocery.item;

import com.davgeoand.api.model.serializer.RecordIdDeserializer;
import com.davgeoand.api.model.serializer.RecordIdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.surrealdb.RecordId;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductOf {
    @JsonSerialize(using = RecordIdSerializer.class)
    @JsonDeserialize(using = RecordIdDeserializer.class)
    public RecordId id, in, out;
}
