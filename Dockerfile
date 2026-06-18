# STAGE 1: Compilation Environment
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build executable jar
COPY src ./src
RUN mvn clean package -DskipTests

# STAGE 2: Minimal Runtime Environment
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy ONLY the compiled jar from stage 1
COPY --from=builder /app/target/*.jar app.jar

# Enforce secure operational practice with non-root user
RUN addgroup -S gcashgroup && adduser -S gcashuser -G gcashgroup
USER gcashuser

ENTRYPOINT ["java", "-jar", "app.jar"]