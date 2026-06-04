package com.davgeoand.api.monitor.event.type;

import com.davgeoand.api.ServiceProperties;
import com.influxdb.v3.client.Point;
import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfo extends Event {
    private String gitBranch = ServiceProperties.getProperty("git.branch");
    private String gitCommitIdAbbrev = ServiceProperties.getProperty("git.commit.id.abbrev");

    @Override
    public Point toPoint() {
        return Point.measurement("service_info")
                .setField("git.branch", gitBranch)
                .setField("git.commit.id.abbrev", gitCommitIdAbbrev)
                .setTimestamp(time);
    }
}

