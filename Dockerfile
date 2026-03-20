FROM davgeoand9/otel-java-agent:21.0.9_10-2.26.0

COPY ./target/planner-service.jar planner-service.jar
COPY ./target/lib lib

EXPOSE 10000

ENTRYPOINT ["java", "-jar", "planner-service.jar"]