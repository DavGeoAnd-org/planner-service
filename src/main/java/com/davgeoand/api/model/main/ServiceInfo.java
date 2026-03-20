package com.davgeoand.api.model.main;

import com.davgeoand.api.ServiceProperties;
import com.davgeoand.api.model.serializer.RecordIdDeserializer;
import com.davgeoand.api.model.serializer.RecordIdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.surrealdb.RecordId;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfo {
    @JsonSerialize(using = RecordIdSerializer.class)
    @JsonDeserialize(using = RecordIdDeserializer.class)
    RecordId id = new RecordId("serviceInfos", Instant.now().toEpochMilli());
    String cloudAvailabilityZone = ServiceProperties.getProperty("cloud.availability_zone").orElse(null);
    String cloudRegion = ServiceProperties.getProperty("cloud.region").orElse(null);
    String containerId = ServiceProperties.getProperty("container.id").orElse(null);
    String deploymentEnvironment = ServiceProperties.getProperty("deployment.environment").orElse(null);
    String gitBranch = ServiceProperties.getProperty("git.branch").orElse(null);
    String gitCommitIdAbbrev = ServiceProperties.getProperty("git.commit.id.abbrev").orElse(null);
    String javalinVersion = ServiceProperties.getProperty("javalin.version").orElse(null);
    String processRuntimeVersion = ServiceProperties.getProperty("process.runtime.version").orElse(null);
    String serviceFramework = ServiceProperties.getProperty("service.framework").orElse(null);
    String serviceName = ServiceProperties.getProperty("service.name").orElse(null);
    String serviceNamespace = ServiceProperties.getProperty("service.namespace").orElse(null);
    String serviceVersion = ServiceProperties.getProperty("service.version").orElse(null);
}