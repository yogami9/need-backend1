FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Set executable permission on mvnw
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create a non-root user to run the application
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Health check
HEALTHCHECK --interval=30s --timeout=3s CMD wget -q --spider http://localhost:${PORT:-10000}/api/v1/actuator/health || exit 1

# Expose port
EXPOSE ${PORT:-10000}

# Set Spring profile to render for Render deployment
ENV SPRING_PROFILES_ACTIVE=render

ENTRYPOINT ["java", "-jar", "/app/app.jar"]