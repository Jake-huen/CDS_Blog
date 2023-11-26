FROM adoptopenjdk:11-jdk-hotspot-bionic AS builder
WORKDIR /app
COPY gradlew /app/
COPY gradle /app/gradle
COPY . /app/
RUN chmod +x /app/gradlew
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        unzip \
        && rm -rf /var/lib/apt/lists/*
RUN ./gradlew bootJar
FROM adoptopenjdk:11-jdk-hotspot-bionic
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 80
ENTRYPOINT ["java", "-jar", "/app.jar"]