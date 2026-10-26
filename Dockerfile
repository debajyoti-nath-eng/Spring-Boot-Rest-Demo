# Runtime image for the packaged Spring Boot jar.
# Build: mvn -DskipTests package && podman build -t spring-boot-rest-demo:local .
# Run:   podman run --rm -p 8282:8282 spring-boot-rest-demo:local
FROM docker.io/library/eclipse-temurin:21-jre-alpine

# apply Alpine security fixes that the base image tag hasn't picked up yet
RUN apk upgrade --no-cache && addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --chown=app:app target/spring-boot-rest-demo-0.0.1-SNAPSHOT.jar app.jar
USER app

EXPOSE 8282
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s CMD wget -qO- http://localhost:8282/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
