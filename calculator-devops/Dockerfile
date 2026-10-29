# Stage 1: Build the Spring Boot application

FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven configuration and wrapper first
# to take advantage of Docker layer caching.

COPY pom.xml mvnw ./
COPY .mvn/ .mvn/

RUN chmod +x mvnw

# Download dependencies before copying application source.

RUN ./mvnw -B dependency:go-offline

COPY src/ src/

# Run tests and package the executable JAR.

RUN ./mvnw -B clean package

# Stage 2: Run the application using a JRE only

FROM eclipse-temurin:21-jre-jammy AS runtime

# Install curl for the Docker Compose health check.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Run the application as a non-root user.

RUN groupadd --system spring \
    && useradd --system --gid spring --home-dir /app spring

COPY --from=build --chown=spring:spring /app/target/calculator-devops-0.0.1-SNAPSHOT.jar app.jar

USER spring:spring

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
