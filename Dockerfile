FROM davgeoand9/otel-java-agent:21.0.5_11-2.20.0

COPY ./target/planner-service.jar planner-service.jar
COPY ./target/lib lib

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "planner-service.jar"]