# Backend Dockerfile for Pet Adoption System
# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy Maven files
COPY pom.xml ./

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds, run them separately)
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8082 (as configured in application.properties)
EXPOSE 8082

# Set environment variables (can be overridden)
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8082

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
