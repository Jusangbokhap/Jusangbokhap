FROM gradle:8.12.1-jdk17 AS builder
WORKDIR /build

COPY gradle gradle
COPY gradlew settings.gradle build.gradle ./
RUN gradle dependencies --no-daemon

COPY src src
RUN gradle build --no-daemon -x test

FROM eclipse-temurin:17-jdk-focal
WORKDIR /app

COPY --from=builder /build/build/libs/*.jar app.jar

ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]