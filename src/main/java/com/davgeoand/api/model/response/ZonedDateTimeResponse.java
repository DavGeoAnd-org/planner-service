package com.davgeoand.api.model.response;

import java.time.ZonedDateTime;

import com.davgeoand.api.model.serializer.ZonedDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
