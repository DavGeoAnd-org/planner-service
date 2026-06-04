package com.davgeoand.api.monitor.event.type;

import com.influxdb.v3.client.Point;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class Event {
    protected Instant time = Instant.now();

    public abstract Point toPoint();
}
