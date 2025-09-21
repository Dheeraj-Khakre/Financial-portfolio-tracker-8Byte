# ---- Stage 1: Build JAR ----
FROM maven:3.9.6-eclipse-temurin-21 AS builder


# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests


# ---- Stage 2: Run JAR ----
FROM eclipse-temurin:21-jdk-alpine


WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]