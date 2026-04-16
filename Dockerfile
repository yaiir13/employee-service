FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
# Copy only wrapper + dependency descriptor first.
# Docker caches this layer — won't re-download dependencies unless build files change.
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon --quiet
# Copy source and build the bootJar (tests skipped — should run in CI pipeline)
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test