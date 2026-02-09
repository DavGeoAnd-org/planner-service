package com.davgeoand.api.model.response;

import com.davgeoand.api.model.serializer.ZonedDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ZonedDateTimeResponse {
    String message;
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    ZonedDateTime timestamp;
}
